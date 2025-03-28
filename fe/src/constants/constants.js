const API_BASE = import.meta.env.VITE_APP_API_URL;

export const SERVER_BASE = `${API_BASE}/api`;

export const JWT = "token";
export const NAME = "nameSurname";
export const TYPE = "type";

export const HOME = '/';
export const USERS = '/users';
export const LOGIN = `${USERS}/authentication`;
export const REGISTER = `${USERS}/register`;
export const PROFILE = `${USERS}/me`;
export const LOGOUT = `${USERS}/logout`;
export const JOB_PROFILES = '/job-profiles';
export const SIGN_REQUESTS = '/signrequests';
export const CONFIG = '/config';
export const FILES = '/files';
export const SIGN_REQUEST_CHRONOLOGIES = '/sign-request-chronologies';
export const SECTORS = '/sectors';
export const LOGIN_DATA = '/logindata';
export const DOC_TEMPLATES = '/doctemplates';
export const CONTRACTS = '/contracts';
export const COMPANIES = '/companies';
export const CITIES = '/cities';
export const PROVINCES = `${CITIES}/provinces`;
export const BRANCHES = '/branches';
export const BASE_SALARIES = '/basesalaries';
export const DASHBOARD = '/dashboard';


export const EMPTY_USER_PROFILE = {
    "id": null,
    "username": "",
    "name": "",
    "surname": "",
    "sex": "M",
    "fiscalcode": "",
    "birthDate": "",
    "birthPlace": "",
    "birthProvince": "",
    "nationality": "",
    "livingCity": "",
    "livingProvince": "",
    "address": "",
    "addressco": "",
    "zipCode": "",
    "phone": "",
    "cellphone": "",
    "email": "",
    "cap": "",
    "notes": "",
    "createdAt": "",
    "updatedAt": "",
    "passwordUpdatedAt": "",
    "jobProfile": {
        // "id": 0,
        "company": {
            "id": 0,
            "description": "",
            "sector": {
                "id": 0,
                "description": ""
            }
        },
        "branch": {
            "id": 0,
            "description": "",
        },
        "baseSalary": {
            "id": 0,
            "baseSalary": 0,
        },
        "contract": {
            "id": 0,
            "level": "",
            "sector": {
                "id": 0,
                "description": ""
            }
        },
        "hoursPerWeek": 0,
        "jobProfileType": "",
        "startDate": "",
        "endDate": "",
        "jobDescription": "",
    },
};
