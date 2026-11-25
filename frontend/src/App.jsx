import { BrowserRouter, Routes, Route, Navigate, Link, useNavigate, useLocation } from "react-router-dom";
import RotaPrivada from "./components/RotaPrivada";
import Login from "./pages/Login";
import ListaProvas from "./pages/ListaProvas";
import FazerProva from "./pages/FazerProva";
import Resultado from "./pages/Resultado";
import MinhasNotas from "./pages/MinhasNotas";
import CriarProva from "./pages/CriarProva";

function Topo() {
  useLocation(); 
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const nome = localStorage.getItem("nome");
  const perfil = localStorage.getItem("perfil");

  if (!token) return null;

  function sair() {
    localStorage.clear();
    navigate("/login");
  }

  return (
    <header className="topo">
      <Link to="/provas" className="logo">Sistema de Provas</Link>
      <nav>
        <Link to="/provas">Provas</Link>
        {perfil === "ALUNO" && <Link to="/minhas-notas">Minhas notas</Link>}
        {perfil === "PROFESSOR" && <Link to="/provas/nova">Nova prova</Link>}
        <span className="usuario">{nome}</span>
        <button className="secundario" onClick={sair}>Sair</button>
      </nav>
    </header>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Topo />
      <main className="conteudo">
        <Routes>
          <Route path="/" element={<Navigate to="/provas" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/provas" element={<RotaPrivada><ListaProvas /></RotaPrivada>} />
          <Route path="/provas/nova" element={<RotaPrivada perfil="PROFESSOR"><CriarProva /></RotaPrivada>} />
          <Route path="/provas/:id" element={<RotaPrivada perfil="ALUNO"><FazerProva /></RotaPrivada>} />
          <Route path="/resultado" element={<RotaPrivada perfil="ALUNO"><Resultado /></RotaPrivada>} />
          <Route path="/minhas-notas" element={<RotaPrivada perfil="ALUNO"><MinhasNotas /></RotaPrivada>} />
          <Route path="*" element={<Navigate to="/provas" replace />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}