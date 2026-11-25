import { Link, Navigate, useLocation } from "react-router-dom";

export default function Resultado() {
  const { state } = useLocation();

  
  if (!state) return <Navigate to="/provas" replace />;

  return (
    <div className="cartao" style={{ textAlign: "center" }}>
      <h2>{state.prova}</h2>
      <p>Sua nota</p>
      <div className="nota">{Number(state.nota).toFixed(2).replace(".", ",")}</div>
      <p>Você acertou {state.acertos} de {state.total} questões.</p>
      <div className="acoes" style={{ justifyContent: "center" }}>
        <Link to="/provas"><button>Voltar para as provas</button></Link>
        <Link to="/minhas-notas"><button className="secundario">Ver minhas notas</button></Link>
      </div>
    </div>
  );
}