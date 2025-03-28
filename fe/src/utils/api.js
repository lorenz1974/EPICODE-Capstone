import { SERVER_BASE } from '@constants';
import axios from 'axios';
import { configure, checkToken } from '@utils/auth';
import { msgResponse, msgLogout } from '@utils/functions';
import { checkErrorResponse } from '@utils/validators';

// Configura un'istanza Axios predefinita
const apiClient = axios.create({
    baseURL: SERVER_BASE,
    headers: configure(),
});

apiClient.interceptors.request.use((config) => {
    if (checkToken()) {
        console.error('Token problem. May be it\'s expired');
    }
    config.headers = configure();
    return config;
});

apiClient.interceptors.response.use(
    (response) => {
        if (checkToken()) {
            response.data.logout = true;
            return response.data;
        }
        console.log("API response: ", response);
        response.data.isSuccess = true;
        return response.data;
    },
    (error) => {
        console.log("API Error: ", error);
        return Promise.reject(checkErrorResponse(error))
    }
);

export const postData = async (path, body = null, params = null) => {
    try {
        return await apiClient.post(path, body, { params });
    } catch (error) {
        if (error.message.startsWith('JWTERR')) {
            return msgLogout();
        }
        throw error;
    }
};

export const putData = async (path, body = null, params = null) => {
    try {
        return await apiClient.put(path, body, { params });
    } catch (error) {
        if (error.message.startsWith('JWTERR')) {
            return msgLogout();
        }
        throw error;
    }
};

export const deleteData = async (path, params = null) => {
    try {
        await apiClient.delete(path, { params });
        return msgResponse();
    } catch (error) {
        if (error.message.startsWith('JWTERR')) {
            return msgLogout();
        }
        throw error;
    }
};

export const getData = async (path, params = null) => {
    try {
        return await apiClient.get(path, { params });
    } catch (error) {
        if (error.message.startsWith('JWTERR')) {
            return msgLogout();
        }
        throw error;
    }
};