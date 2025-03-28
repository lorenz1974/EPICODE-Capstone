package capstone.be.appuser;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

        // Method needed for performance issue in the getAll method. It's better to use
        // a 'light' dto because it's not possible to avoiu the Fecth.EAGER on te
        // AppUser entity
        @Query(value = "SELECT new capstone.be.appuser.AppUserGetAllResponse(" +
                        "u.id, u.username, u.name, u.surname, u.fiscalcode, u.sex, u.birthDate, " +
                        "u.birthPlace, u.birthProvince, u.livingCity, u.livingProvince, u.address, " +
                        "u.zipCode, u.phone, u.cellphone, u.email, u.cap, u.mail, u.notes, " +
                        "u.nameSurname, u.surnameName, u.jobProfile, u.isDeleted) " +
                        "FROM AppUser u", countQuery = "SELECT COUNT(u) FROM AppUser u")
        Page<AppUserGetAllResponse> findAllLight(Pageable pageable);

        boolean existsByEmailIgnoreCase(String email);

        Optional<AppUser> findByEmailIgnoreCase(String email);

        boolean existsByUsernameIgnoreCase(String username);

        Optional<AppUser> findByUsernameIgnoreCase(String username);

        boolean existsByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

        Optional<AppUser> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

        boolean existsByFiscalcodeIgnoreCase(String fiscalcode);

        Optional<AppUser> findByFiscalcodeIgnoreCase(String fiscalcode);

        static final String WHERE_CLAUSE = """
                        (LOWER(u.username) LIKE %:q%) OR
                        (LOWER(u.name) LIKE %:q%) OR
                        (LOWER(u.surname) LIKE %:q%) OR
                        (LOWER(u.fiscalcode) LIKE %:q%) OR
                        (LOWER(u.nationality) LIKE %:q%) OR
                        (LOWER(u.birthProvince) LIKE %:q%) OR
                        (LOWER(u.birthPlace) LIKE %:q%) OR
                        (LOWER(u.livingProvince) LIKE %:q%) OR
                        (LOWER(u.livingCity) LIKE %:q%) OR
                        (LOWER(u.address) LIKE %:q%) OR
                        (LOWER(u.zipCode) LIKE %:q%) OR
                        (LOWER(u.phone) LIKE %:q%) OR
                        (LOWER(u.cellphone) LIKE %:q%) OR
                        (LOWER(u.email) LIKE %:q%) OR
                        (LOWER(u.cap) LIKE %:q%) OR
                        (LOWER(u.nameSurname) LIKE %:q%) OR
                        (LOWER(u.surnameName) LIKE %:q%) OR
                        (LOWER(u.notes) LIKE %:q%)
                        """;
        // OR
        // (LOWER(u.jobProfile.company.description) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.alternativeDescription) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.city) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.province) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.cap) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.address) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.phone) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.piva) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.notes) LIKE %:q%) OR
        // (LOWER(u.jobProfile.company.mail) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.description) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.alternativeDescription) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.city) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.province) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.cap) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.address) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.phone) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.piva) LIKE %:q%) OR
        // (LOWER(u.jobProfile.branch.mail) LIKE %:q%)
        // """;

        @Query(value = "SELECT new capstone.be.appuser.AppUserGetAllResponse(" +
                        "u.id, u.username, u.name, u.surname, u.fiscalcode, u.sex, u.birthDate, " +
                        "u.birthPlace, u.birthProvince, u.livingCity, u.livingProvince, u.address, " +
                        "u.zipCode, u.phone, u.cellphone, u.email, u.cap, u.mail, u.notes, " +
                        "u.nameSurname, u.surnameName, u.jobProfile, u.isDeleted) " +
                        "FROM AppUser u WHERE "
                        + WHERE_CLAUSE, countQuery = "SELECT COUNT(u) FROM AppUser u WHERE " + WHERE_CLAUSE)
        Page<AppUserGetAllResponse> omniSearch(@Param("q") String q, Pageable pageable);

        Page<AppUser> findByRolesContaining(AppUserRole userRole, Pageable pageable);

        @Query("SELECT u FROM AppUser u WHERE " +
                        "u.jobProfile.branch.id = ?1")
        Page<AppUser> getBranchById(Long id, Pageable pageable);
}