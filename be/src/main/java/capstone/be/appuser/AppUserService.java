package capstone.be.appuser;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import capstone.be.auth.JwtTokenUtil;
import capstone.be.auth.SecurityUtils;
import capstone.be.basesalary.BaseSalaryRepository;
import capstone.be.branch.BranchRepository;
import capstone.be.city.CityRepository;
import capstone.be.company.CompanyRepository;
import capstone.be.contract.ContractRepository;
import capstone.be.jobprofile.JobProfile;
import capstone.be.jobprofile.JobProfileRequest;
import capstone.be.jobprofile.JobProfileService;
import capstone.be.logindata.LoginDataService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.util.Random;
import java.util.regex.Pattern;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final LoginDataService loginDataService;
    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;
    private final SecurityUtils securityUtils;
    private final JobProfileService jobProfileService;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final ContractRepository contractRepository;
    private final BaseSalaryRepository baseSalaryRepository;

    //
    // Do not use findUserByIdOrUsernameOrEmail() method in this method
    // for security reasons
    //
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    //
    // Method with AppUser as parameter
    //
    public AppUser checkAppUser(AppUser appUser, boolean create)
            throws EntityExistsException, IllegalArgumentException, EntityNotFoundException {

        log.info("Saving user: " + appUser);

        // Check the username and email (they must be unique) if it is a new user
        // when you create or update a user. When you update a user, the username and
        // email must be unique only if they are different from the original ones
        if (appUserRepository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(appUser.getUsername(),
                appUser.getEmail())) {

            AppUser existingUser = appUserRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(appUser.getUsername(),
                    appUser.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "Updating user username or email not found and this is a very strange thing at this point..."));
            if (existingUser != (appUser)) {
                throw new EntityExistsException(
                        "Username or email already exists. You can set the same username and email of an existing user.");
            }
        }

        // Check the fiscalcode (it must be unique) if it is a new user
        // when you create or update a user
        if (appUserRepository.existsByFiscalcodeIgnoreCase(appUser.getFiscalcode())) {
            AppUser existingUser = appUserRepository.findByFiscalcodeIgnoreCase(appUser.getFiscalcode())
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "Updating user fiscal code not found and this is a very strange thing at this point..."));

            if (existingUser.getId() != appUser.getId()) {
                throw new EntityExistsException(
                        "Fiscalcode already exists. You can set the same fiscalcode of an existing user.");
            }
        }

        // Check for birthPlace and birthProvince are correct
        if (appUser.getBirthPlace() != null && !appUser.getBirthPlace().isEmpty()
                || appUser.getBirthProvince() != null && !appUser.getBirthProvince().isEmpty()) {

            if (!cityRepository.existsByCityStateAndProvince(appUser.getBirthPlace(), appUser.getBirthProvince())) {
                throw new IllegalArgumentException("Birth place and birth province are not correct");
            }
        }

        // Check for livingCity and livingProvince are correct
        if (appUser.getLivingCity() != null && !appUser.getLivingCity().isEmpty()
                || appUser.getLivingProvince() != null && !appUser.getLivingProvince().isEmpty()) {

            if (!cityRepository.existsByCityStateAndProvince(appUser.getLivingCity(), appUser.getLivingProvince())) {
                throw new IllegalArgumentException("Living city and living province are not correct");
            }
        }

        // Check the fatherId or it will find using th JWT code od the user how made the
        // request
        if (appUser.getFatherId() == null || appUser.getFatherId() <= 0) {
            AppUser father = getCurrentUser();
            appUser.setFatherId(father.getId());
        }

        // Creare a default username if it is null or empty
        appUser.setUsername(appUser.getUsername() == null || appUser.getUsername().isEmpty()
                ? appUser.getEmail().substring(0, appUser.getEmail().indexOf('@'))
                : appUser.getUsername());

        // Set the default role to ROLE_USER if roles are null or empty
        appUser.setRoles((appUser.getRoles() == null || appUser.getRoles().isEmpty()) ? Set.of(AppUserRole.ROLE_USER)
                : appUser.getRoles());

        // Set the default password to a random password if it is null or empty
        if (appUser.getPassword() == null || appUser.getPassword().isEmpty()) {
            appUser.setPassword(generateRandomPassword(20));
        } else if (create) {
            // ... or check if the password is valid only during the creation of the user
            String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[.,;:_#@$!%*?&])[A-Za-z\\d.,;:_#@$!%*?&]{8,}$";
            Pattern pattern = Pattern.compile(passwordRegex);

            if (!pattern.matcher(appUser.getPassword()).matches()) {
                throw new IllegalArgumentException(
                        "The password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number, and one special character.");
            }
        }

        // Encode the password if it is a new user
        if (create) {
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        }

        // Set the fiscalCode to uppercase
        appUser.setFiscalcode(appUser.getFiscalcode().toUpperCase());

        // return the checked AppUser
        return appUser;
    }

    //
    // Method to save an AppUser
    //
    public AppUser saveUser(AppUser appUser, boolean create) {
        // Validate AppUser
        AppUser checkedUser = checkAppUser(appUser, create);

        // Validate JobProfile (already updated with correct data)
        JobProfile profile = checkedUser.getJobProfile();
        jobProfileService.checkJobProfile(profile, true);

        // Save and return
        AppUser saved = appUserRepository.save(checkedUser);
        return saved;
    }

    //
    // Method with AppUserRegistrationRequest as parameter
    //
    public AppUser registerUser(@Valid AppUserRegistrationRequest appUserRegistrationRequest) {
        // Map the registration appUserRegistrationRequest to a new AppUser entity
        AppUser appUser = new AppUser();
        modelMapper.map(appUserRegistrationRequest, appUser);

        // Create and link an empty JobProfile instance to the AppUser
        JobProfileRequest jobProfileRequest = appUserRegistrationRequest.getJobProfile();
        JobProfile jobProfile = new JobProfile();
        appUser.setJobProfile(jobProfile);
        jobProfile.setAppUser(appUser);

        // Use shared logic to populate and validate the JobProfile
        updateJobProfile(appUser, jobProfileRequest);

        // Save the user (create = true)
        return saveUser(appUser, true);
    }

    //
    // Method with AppUser as parameter
    //
    public AppUser updateUser(@Valid Long id, @Valid AppUser appUser) {
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Map basic fields
        modelMapper.map(appUser, existingUser);

        // Manually extract JobProfile into DTO
        JobProfile profile = appUser.getJobProfile();
        JobProfileRequest jobProfileRequest = new JobProfileRequest();

        jobProfileRequest.setAppUserId(id);
        jobProfileRequest.setCompanyId(profile.getCompany().getId());
        jobProfileRequest.setBranchId(profile.getBranch().getId());
        jobProfileRequest.setContractId(profile.getContract().getId());
        jobProfileRequest.setBaseSalaryId(profile.getBaseSalary().getId());
        jobProfileRequest.setHoursPerWeek(profile.getHoursPerWeek());
        jobProfileRequest.setJobProfileType(profile.getJobProfileType());
        jobProfileRequest.setStartDate(profile.getStartDate());
        jobProfileRequest.setEndDate(profile.getEndDate());
        jobProfileRequest.setJobDescription(profile.getJobDescription());

        // Delegate to shared logic
        updateJobProfile(existingUser, jobProfileRequest);

        return saveUser(existingUser, false);
    }

    //
    // Method with AppUserUpdateUser as parameter
    //
    public AppUser updateUser(@Valid Long id, @Valid AppUserUpdateRequest appUserUpdateRequest) {
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Map basic fields
        modelMapper.map(appUserUpdateRequest, existingUser);

        // Convert JobProfileRequest and assign appUserId
        JobProfileRequest jobProfileRequest = appUserUpdateRequest.getJobProfile();
        jobProfileRequest.setAppUserId(id);

        // Delegate to shared logic
        updateJobProfile(existingUser, jobProfileRequest);

        return saveUser(existingUser, false);
    }

    //
    // Method to update the JobProfile for a user
    //
    private void updateJobProfile(AppUser appUser, JobProfileRequest jobProfileRequest) {
        JobProfile profile = appUser.getJobProfile();

        if (profile == null) {
            throw new IllegalStateException("JobProfile expected but not found for appUser id: " + appUser.getId());
        }

        // Set relational entities from IDs
        profile.setAppUser(appUser);
        profile.setCompany(companyRepository.findById(jobProfileRequest.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company not found with ID: " + jobProfileRequest.getCompanyId())));
        profile.setBranch(branchRepository.findById(jobProfileRequest.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Branch not found with ID: " + jobProfileRequest.getBranchId())));
        profile.setContract(contractRepository.findById(jobProfileRequest.getContractId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contract not found with ID: " + jobProfileRequest.getContractId())));
        profile.setBaseSalary(baseSalaryRepository.findById(jobProfileRequest.getBaseSalaryId())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "BaseSalary not found with ID: " + jobProfileRequest.getBaseSalaryId())));

        // Set primitive fields
        profile.setHoursPerWeek(jobProfileRequest.getHoursPerWeek());
        profile.setJobProfileType(jobProfileRequest.getJobProfileType());
        profile.setStartDate(jobProfileRequest.getStartDate());
        profile.setEndDate(jobProfileRequest.getEndDate());
        profile.setJobDescription(jobProfileRequest.getJobDescription());

        // MAY BE REDUNDANT
        // Business logic to validate the JobProfile
        // jobProfileService.checkJobProfile(profile, false);
    }

    public AppUserAuthResponse authenticateUser(String username, String password) {

        UserDetails userDetails;
        AppUserAuthResponse appUserAuthResponse = new AppUserAuthResponse();

        // Check if the user exists
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            String message = "Login failed for '" + username + "': User not found";
            loginDataService.pushLoginData("FAILED", username, null, message, "LOGIN");
            throw e;
        }

        // Log the login attempt
        String message = "Login attempt by '" + username + "'";
        loginDataService.pushLoginData("TRYING", username, null, message, "LOGIN");

        // Authenticate the user
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            message = "Login successful for '" + username + "'";
            loginDataService.pushLoginData("SUCCESS", username, token, message, "LOGIN");

            // Get all the user properties to compose the auth response
            AppUser appUser = appUserRepository.findByUsernameIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Set the properties to the response object
            appUserAuthResponse.setUserId(appUser.getId());
            appUserAuthResponse.setUsername(appUser.getUsername());
            appUserAuthResponse.setEmail(appUser.getEmail());
            appUserAuthResponse.setName(appUser.getName());
            appUserAuthResponse.setSurname(appUser.getSurname());
            appUserAuthResponse.setNameSurname(appUser.getNameSurname());
            appUserAuthResponse.setRoles(appUser.getRoles());
            appUserAuthResponse.setToken(token);
            appUserAuthResponse.setDocTemplatesAllowed(appUser.getDocTemplatesAllowed());

            // Return the response object
            return appUserAuthResponse;

        } catch (BadCredentialsException e) {
            message = "Login failed for '" + username + "': Invalid credentials";
            loginDataService.pushLoginData("FAILED", username, null, message, "LOGIN");
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        AppUser appUser = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, appUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        appUser.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(appUser);
    }

    public void updateUserRoles(String username, Set<AppUserRole> roles) {
        AppUser appUser = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        appUser.setRoles(roles);
        appUserRepository.save(appUser);
    }

    public AppUser getUserByIdOrUsernameOrEmail(String idOrUsernameOrEmail) {
        return findUserByIdOrUsernameOrEmail(idOrUsernameOrEmail);
    }

    //
    // Method that returns the current user
    // It uses the findUserByIdOrUsernameOrEmail method
    // because username could change in the future to the email
    //
    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return findUserByIdOrUsernameOrEmail(authentication.getName());
    }

    //
    // Method with a string and a boolean as parameters
    // it uses the findUserByIdOrUsernameOrEmail method
    //
    // Only admins can delete a user
    //
    public void deleteUser(Long id,
            boolean forceDeletion) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User ID not found"));
        if (forceDeletion && securityUtils.isAdmin()) {
            appUserRepository.delete(appUser);
        } else {
            appUser.setDeleted(true);
            appUserRepository.save(appUser);
        }
    }

    //
    // Default repository method
    //
    public AppUser getUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User ID not found"));
    }

    public Page<AppUserGetAllResponse> getAllUsers(Pageable pageable, String q, boolean alsoDeleted) {
        log.debug("Fetching users with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return appUserRepository.findAllLight(pageable);
        }
        return appUserRepository.omniSearch(q.toLowerCase(), pageable);
    }

    //
    // Method that searches for a user by username or email or id
    // It is used also in the deleteUser method
    // Not crearted in the repository because it's not a standard method
    // and idOrUsernameOrEmail could be a String or a long
    //
    public AppUser findUserByIdOrUsernameOrEmail(String idOrUsernameOrEmail) {
        try {
            Long id = Long.valueOf(idOrUsernameOrEmail);
            return appUserRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User id not found"));
        } catch (NumberFormatException e) {
            // convert to lowercase to avoid case sensitivity
            idOrUsernameOrEmail = idOrUsernameOrEmail.toLowerCase();
            return appUserRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(
                    idOrUsernameOrEmail,
                    idOrUsernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found"));
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
        StringBuilder password = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public Page<AppUser> getBranchById(Pageable pageable, Long id) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort());

        return appUserRepository.getBranchById(id, sortedPageable);
    }
}
