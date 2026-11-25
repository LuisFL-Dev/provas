import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api, { mensagemDeErro } from "../api/axios";

// Uma questão em branco, com 2 alternativas (a primeira já marcada como correta)
function novaQuestao() {
  return {
    enunciado: "",
    alternativas: [
      { texto: "", correta: true },
      { texto: "", correta: false },
    ],
  };
}

export default function CriarProva() {
  const navigate = useNavigate();
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [questoes, setQuestoes] = useState([novaQuestao()]);
  const [erro, setErro] = useState("");
  const [salvando, setSalvando] = useState(false);

  
  function copiarQuestoes() {
    return structuredClone(questoes);
  }

  

  function adicionarQuestao() {
    const novas = copiarQuestoes();
    novas.push(novaQuestao());
    setQuestoes(novas);
  }

  function removerQuestao(qi) {
    const novas = copiarQuestoes();
    novas.splice(qi, 1);
    setQuestoes(novas);
  }

  function alterarEnunciado(qi, texto) {
    const novas = copiarQuestoes();
    novas[qi].enunciado = texto;
    setQuestoes(novas);
  }


  function adicionarAlternativa(qi) {
    const novas = copiarQuestoes();
    novas[qi].alternativas.push({ texto: "", correta: false });
    setQuestoes(novas);
  }

  function removerAlternativa(qi, ai) {
    const novas = copiarQuestoes();
    const eraCorreta = novas[qi].alternativas[ai].correta;
    novas[qi].alternativas.splice(ai, 1);
    if (eraCorreta) {
      novas[qi].alternativas[0].correta = true; 
    }
    setQuestoes(novas);
  }

  function alterarTextoAlternativa(qi, ai, texto) {
    const novas = copiarQuestoes();
    novas[qi].alternativas[ai].texto = texto;
    setQuestoes(novas);
  }

  function marcarCorreta(qi, ai) {
    const novas = copiarQuestoes();
    for (let i = 0; i < novas[qi].alternativas.length; i++) {
      novas[qi].alternativas[i].correta = (i === ai);
    }
    setQuestoes(novas);
  }



  async function salvar(e) {
    e.preventDefault();
    setErro("");
    setSalvando(true);
    try {
      await api.post("/provas", { titulo, descricao, questoes });
      navigate("/provas");
    } catch (error) {
      setErro(mensagemDeErro(error));
      setSalvando(false);
    }
  }

  return (
    <form onSubmit={salvar}>
      <h2>Nova prova</h2>

      <div className="cartao">
        <label>Título</label>
        <input type="text" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
        <label>Descrição (opcional)</label>
        <textarea rows={2} value={descricao} onChange={(e) => setDescricao(e.target.value)} />
      </div>

      {questoes.map((questao, qi) => (
        <div key={qi} className="cartao">
          <label>Questão {qi + 1}</label>
          <textarea
            rows={2}
            value={questao.enunciado}
            onChange={(e) => alterarEnunciado(qi, e.target.value)}
            required
          />

          <p>Alternativas (marque a correta):</p>
          {questao.alternativas.map((alternativa, ai) => (
            <div key={ai} className="alternativa">
              <input
                type="radio"
                name={`correta-${qi}`}
                checked={alternativa.correta}
                onChange={() => marcarCorreta(qi, ai)}
              />
              <input
                type="text"
                value={alternativa.texto}
                onChange={(e) => alterarTextoAlternativa(qi, ai, e.target.value)}
                placeholder={`Alternativa ${ai + 1}`}
                required
              />
              {questao.alternativas.length > 2 && (
                <button type="button" className="secundario" onClick={() => removerAlternativa(qi, ai)}>
                  X
                </button>
              )}
            </div>
          ))}

          <div className="acoes">
            <button type="button" className="secundario" onClick={() => adicionarAlternativa(qi)}>
              + Alternativa
            </button>
            {questoes.length > 1 && (
              <button type="button" className="perigo" onClick={() => removerQuestao(qi)}>
                Remover questão
              </button>
            )}
          </div>
        </div>
      ))}

      <div className="acoes">
        <button type="button" className="secundario" onClick={adicionarQuestao}>
          + Questão
        </button>
        <button type="submit" disabled={salvando}>
          {salvando ? "Salvando..." : "Salvar prova"}
        </button>
      </div>

      {erro && <div className="erro">{erro}</div>}
    </form>
  );
}