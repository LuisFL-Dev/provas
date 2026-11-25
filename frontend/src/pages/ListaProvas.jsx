import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { mensagemDeErro } from "../api/axios";

export default function ListaProvas() {
  const perfil = localStorage.getItem("perfil");
  const [provas, setProvas] = useState([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  async function carregar() {
    try {
      const { data } = await api.get("/provas");
      setProvas(data);
    } catch (error) {
      setErro(mensagemDeErro(error));
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  async function excluir(id) {
    if (!window.confirm("Excluir esta prova?")) return;
    try {
      await api.delete(`/provas/${id}`);
      carregar();
    } catch (error) {
      setErro(mensagemDeErro(error));
    }
  }

  if (carregando) return <p>Carregando...</p>;

  return (
    <div>
      <h2>Provas</h2>
      {erro && <div className="erro">{erro}</div>}
      {provas.length === 0 && <p>Nenhuma prova cadastrada ainda.</p>}

      {provas.map((prova) => (
        <div key={prova.id} className="cartao">
          <h3>{prova.titulo}</h3>
          {prova.descricao && <p>{prova.descricao}</p>}
          <p>{prova.totalQuestoes} questão(ões)</p>

          <div className="acoes">
            {perfil === "ALUNO" && (
              <Link to={`/provas/${prova.id}`}>
                <button>Fazer prova</button>
              </Link>
            )}
            {perfil === "PROFESSOR" && (
              <button className="perigo" onClick={() => excluir(prova.id)}>Excluir</button>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}