import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import api, { mensagemDeErro } from "../api/axios";

export default function Login() {
  const navigate = useNavigate();
  const [modo, setModo] = useState("login"); 
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  
  if (localStorage.getItem("token")) {
    return <Navigate to="/provas" replace />;
  }

  async function enviar(e) {
    e.preventDefault();
    setErro("");
    setCarregando(true);
    try {
      const url = modo === "login" ? "/auth/login" : "/auth/registro";
      const dados = modo === "login" ? { email, senha } : { nome, email, senha };
      const { data } = await api.post(url, dados);

      localStorage.setItem("token", data.token);
      localStorage.setItem("nome", data.nome);
      localStorage.setItem("perfil", data.perfil);
      navigate("/provas", { replace: true });
    } catch (error) {
      setErro(mensagemDeErro(error));
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="cartao" style={{ maxWidth: 400, margin: "60px auto" }}>
      <h2>{modo === "login" ? "Entrar" : "Criar conta de aluno"}</h2>

      <form onSubmit={enviar}>
        {modo === "cadastro" && (
          <>
            <label>Nome</label>
            <input type="text" value={nome} onChange={(e) => setNome(e.target.value)} required />
          </>
        )}

        <label>E-mail</label>
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />

        <label>Senha</label>
        <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required />

        {erro && <div className="erro">{erro}</div>}

        <div className="acoes">
          <button type="submit" disabled={carregando}>
            {carregando ? "Aguarde..." : modo === "login" ? "Entrar" : "Cadastrar"}
          </button>
          <button
            type="button"
            className="secundario"
            onClick={() => { setModo(modo === "login" ? "cadastro" : "login"); setErro(""); }}
          >
            {modo === "login" ? "Criar conta" : "Já tenho conta"}
          </button>
        </div>
      </form>
    </div>
  );
}