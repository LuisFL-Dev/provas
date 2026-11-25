import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

// Coloca o token em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Token vencido ou inválido: volta para o login
api.interceptors.response.use(
  (resposta) => resposta,
  (error) => {
    const ehLogin = error.config?.url?.includes("/auth/");
    if (error.response?.status === 401 && !ehLogin) {
      localStorage.clear();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// Pega a mensagem {"erro": "..."} que a API devolve
export function mensagemDeErro(error) {
  return error.response?.data?.erro || "Não foi possível falar com o servidor";
}

export default api;