package capstone.be.datacreationrunners;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import capstone.be.branch.BranchRepository;
import capstone.be.branch.BranchRequest;
import capstone.be.city.City;
import capstone.be.city.CityRepository;
import capstone.be.company.CompanyRepository;
import capstone.be.company.CompanyRequest;
import capstone.be.configmanager.ConfigManager;
import capstone.be.sectors.Sector;
import capstone.be.sectors.SectorRepository;
import capstone.be.utility.CsvImporter;
import capstone.be.contract.ContractRepository;
import capstone.be.contract.ContractRequest;
import capstone.be.basesalary.BaseSalaryRepository;
import capstone.be.basesalary.BaseSalaryRequest;

import org.springframework.beans.BeanUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(2)
@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
public class DataImporter implements CommandLineRunner {

        private final CsvImporter csvImporter;
        private final ConfigManager configManager;

        private final CityRepository cityRepository;
        private final SectorRepository sectorRepository;
        private final CompanyRepository companyRepository;
        private final BranchRepository branchRepository;
        private final ContractRepository contractRepository;
        private final BaseSalaryRepository baseSalaryRepository;

        @Value("${spring.jpa.hibernate.ddl-auto}")
        private String ddlAuto;

        @Override
        public void run(String... args) throws Exception {
                // Check if table creation is needed
                log.info("*** spring.jpa.hibernate.ddl-auto set to '{}'. {}", ddlAuto,
                                (ddlAuto.equals("none") || ddlAuto.equals("update")) ? "Skipping records creation..."
                                                : "Creating records...");
                if (ddlAuto.equals("none") || ddlAuto.equals("update")) {
                        return;
                }

                importSectors();
                importCompanies();
                importBranches();
                importContracts();
                importBaseSalaries();
                importCities();
        }

        //
        // Method to Import cities from CSV file
        //
        private void importCities() {
                String cityCsvFile = (String) configManager.getConfVariable("cityCsvFile");

                log.info("Importing cities from file: {}", cityCsvFile);

                List<City> cities = csvImporter
                                .importCsv(cityCsvFile, City.class);

                log.info("Import cities...");

                int citiesSize = cities.size();
                log.info("Cities: {}", citiesSize);
                log.debug("Cities: {}", cities.toString());

                int i = 0;
                for (City city : cities) {
                        //
                        // Limit the number of cities to import
                        // This is useful for testing
                        //
                        // if (i == 2000) {
                        // break;
                        // }

                        try {
                                City newCity = new City();
                                BeanUtils.copyProperties(city, newCity);

                                // Foreign stateshave no city and province so I set them to 'EE' and 'EE'
                                // They have cityState and state fields equal
                                if (newCity.getCityState().equals(newCity.getState())) {
                                        newCity.setProvince("EE");
                                        newCity.setRegion("EE");
                                }
                                // Save the cityState
                                cityRepository.save(newCity);
                                if (i % 500 == 0) {
                                        log.info("Saved city {} of {}: {}", i, citiesSize, newCity);
                                }
                        } catch (Exception e) {
                                log.error("Error saving city: {}", city, e);
                        }
                        i++;
                }
                log.info("Done!");
        }

        //
        // Method to Import sectors from CSV file
        //
        private void importSectors() {
                String sectorCsvFile = (String) configManager.getConfVariable("sectorsCsvFile");

                log.info("Importing sectors from file: {}", sectorCsvFile);

                List<Sector> sectors = csvImporter
                                .importCsv(sectorCsvFile, Sector.class);

                log.info("Import sectors...");

                int sectorsSize = sectors.size();
                log.info("Sectors: {}", sectorsSize);
                log.debug("Sectors: {}", sectors.toString());

                int i = 0;
                for (Sector sector : sectors) {
                        try {
                                // Devo importarli in maniera brutale per non far generare l'id a JPA
                                sectorRepository.insertSector(sector.getId(), sector.getDescription());
                                log.info("Saved sector {} of {}: {}", i, sectorsSize, sector);

                        } catch (Exception e) {
                                log.error("Error saving sector: {}", sector, e);
                        }
                        i++;
                }
                log.info("Done!");
        }

        private void importCompanies() {
                String companiesCsvFile = (String) configManager.getConfVariable("companiesCsvFile");

                log.info("Importing companies from file: {}", companiesCsvFile);

                log.info("Import companies...");

                List<CompanyRequest> companies = csvImporter
                                .importCsv(companiesCsvFile, CompanyRequest.class);

                int companiesSize = companies.size();
                log.info("Companies: {}", companiesSize);

                int i = 0;
                for (CompanyRequest company : companies) {
                        try {
                                Long sectorId = company.getSector_id() != 0 ? company.getSector_id() : null;
                                companyRepository.insertCompany(company.getId(), company.getDescription(),
                                                company.getAlternativeDescription(), company.getCity(),
                                                company.getProvince(), company.getCap(), company.getAddress(),
                                                company.getPhone(), company.getPiva(), company.getNotes(),
                                                company.getMail(),
                                                sectorId);

                                if (i % 100 == 0) {
                                        log.info("Saved company {} of {}: {}", i, companiesSize, company);
                                }

                        } catch (Exception e) {
                                log.error("Error saving company: {}: {}", company, e.getMessage());
                                return;
                        }
                        i++;
                }
                log.info("Done!");
        }

        private void importBranches() {
                String branchesCsvFile = (String) configManager.getConfVariable("branchesCsvFile");

                log.info("Importing branches from file: {}", branchesCsvFile);

                log.info("Import branches...");

                List<BranchRequest> branches = csvImporter
                                .importCsv(branchesCsvFile, BranchRequest.class);

                int branchesSize = branches.size();
                log.info("Branches: {}", branchesSize);

                int i = 0;
                for (BranchRequest branch : branches) {
                        try {
                                Long companyId = branch.getCompany_Id() != 0 ? branch.getCompany_Id() : null;
                                branchRepository.insertBranch(branch.getId(), branch.getDescription(),
                                                branch.getAlternativeDescription(), branch.getCity(),
                                                branch.getProvince(), branch.getCap(), branch.getAddress(),
                                                branch.getPhone(), branch.getPiva(), branch.getContributionStart(),
                                                branch.getMail(), companyId);

                                if (i % 100 == 0) {
                                        log.info("Saved branch {} of {}: {}", i, branchesSize, branch);
                                }

                        } catch (Exception e) {
                                log.error("Error saving branch: {}: {}", branch, e.getMessage());
                                return;
                        }
                        i++;
                }
                log.info("Done!");
        }

        private void importContracts() {
                String contractsCsvFile = (String) configManager.getConfVariable("contractsCsvFile");

                log.info("Importing contracts from file: {}", contractsCsvFile);

                List<ContractRequest> contracts = csvImporter
                                .importCsv(contractsCsvFile, ContractRequest.class);

                log.info("Import contracts...");

                int contractsSize = contracts.size();
                log.info("Contracts: {}", contractsSize);

                int i = 0;
                for (ContractRequest contract : contracts) {
                        try {
                                Long sectorId = contract.getSector_Id() != 0 ? contract.getSector_Id() : null;
                                contractRepository.insertContract(contract.getLevel(), sectorId);
                                if (i % 100 == 0) {
                                        log.info("Saved contract {} of {}: {}", i, contractsSize, contract);
                                }
                        } catch (Exception e) {
                                log.error("Error saving contract: {}", contract, e);
                                return;
                        }
                        i++;
                }
                log.info("Done!");
        }

        private void importBaseSalaries() {
                String baseSalariesCsvFile = (String) configManager.getConfVariable("baseSalariesCsvFile");

                log.info("Importing base salaries from file: {}", baseSalariesCsvFile);

                List<BaseSalaryRequest> baseSalaries = csvImporter
                                .importCsv(baseSalariesCsvFile, BaseSalaryRequest.class);

                log.info("Import base salaries...");

                int baseSalariesSize = baseSalaries.size();
                log.info("Base Salaries: {}", baseSalariesSize);

                int i = 0;
                for (BaseSalaryRequest baseSalary : baseSalaries) {
                        try {
                                Long contractId = baseSalary.getContract_Id() != 0 ? baseSalary.getContract_Id() : null;
                                baseSalaryRepository.insertBaseSalary(
                                                baseSalary.getBaseSalary(),
                                                baseSalary.getContingency(),
                                                baseSalary.getDueFullTime(),
                                                baseSalary.getValideFrom(),
                                                baseSalary.getValideTo(),
                                                baseSalary.getTredicesima(),
                                                baseSalary.getThirteenMonth(),
                                                baseSalary.getQuattordicesima(),
                                                baseSalary.getFourteenMonth(),
                                                contractId);
                                if (i % 100 == 0) {
                                        log.info("Saved base salary {} of {}: {}", i, baseSalariesSize, baseSalary);
                                }
                        } catch (Exception e) {
                                log.error("Error saving base salary: {}", baseSalary, e);
                                return;
                        }
                        i++;
                }
                log.info("Done!");
        }

}
