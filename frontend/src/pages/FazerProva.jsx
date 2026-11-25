import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api, { mensagemDeErro } from "../api/axios";

const TEMPO_LIMITE = 60 * 60; 


function formatarTempo(segundos) {
  const minutos = String(Math.floor(segundos / 60)).padStart(2, "0");
  const resto = String(segundos % 60).padStart(2, "0");
  return `${minutos}:${resto}`;
}

export default function FazerProva() {
  const { id } = useParams();
  const navigate = useNavigate();
  const chaveInicio = `inicio-prova-${id}`;

  const [prova, setProva] = useState(null);
  const [tentativas, setTentativas] = useState(null); 
  const [respostas, setRespostas] = useState({});      
  const [inicio, setInicio] = useState(null);          
  const [restante, setRestante] = useState(null);      
  const [erro, setErro] = useState("");
  const [enviando, setEnviando] = useState(false);

 
  useEffect(() => {
    Promise.all([
      api.get(`/provas/${id}`),
      api.get(`/provas/${id}/minhas-tentativas`),
    ])
      .then(([respostaProva, respostaTentativas]) => {
        setProva(respostaProva.data);
        setTentativas(respostaTentativas.data);

        
        if (respostaTentativas.data.realizadas < respostaTentativas.data.limite) {
          
          let comecou = localStorage.getItem(chaveInicio);
          if (!comecou) {
            comecou = String(Date.now());
            localStorage.setItem(chaveInicio, comecou);
          }
          setInicio(Number(comecou));
        }
      })
      .catch((error) => setErro(mensagemDeErro(error)));
  }, [id, chaveInicio]);

  
  useEffect(() => {
    if (!inicio) return;

    function atualizar() {
      const passou = Math.floor((Date.now() - inicio) / 1000);
      setRestante(Math.max(TEMPO_LIMITE - passou, 0));
    }

    atualizar();
    const intervalo = setInterval(atualizar, 1000);
    return () => clearInterval(intervalo);
  }, [inicio]);

  
  useEffect(() => {
    if (restante === 0 && !enviando) {
      enviar(true);
    }
  }, [restante]);

  function escolher(questaoId, alternativaId) {
    setRespostas({ ...respostas, [questaoId]: alternativaId });
  }

  async function enviar(tempoEsgotado = false) {
    
    if (!tempoEsgotado && Object.keys(respostas).length < prova.questoes.length) {
      setErro("Responda todas as questões antes de enviar.");
      return;
    }
    setErro("");
    setEnviando(true);
    try {
      const corpo = {
        respostas: Object.entries(respostas).map(([questaoId, alternativaId]) => ({
          questaoId: Number(questaoId),
          alternativaId,
        })),
      };
      const { data } = await api.post(`/provas/${id}/respostas`, corpo);
      localStorage.removeItem(chaveInicio);
      navigate("/resultado", { state: data });
    } catch (error) {
      setErro(mensagemDeErro(error));
      setEnviando(false);
    }
  }

  if (!prova || !tentativas) {
    return erro ? <div className="erro">{erro}</div> : <p>Carregando...</p>;
  }

  
  if (tentativas.realizadas >= tentativas.limite) {
    return (
      <div className="cartao" style={{ textAlign: "center" }}>
        <h2>{prova.titulo}</h2>
        <p>Você já usou as {tentativas.limite} tentativas desta prova.</p>
        <Link to="/minhas-notas"><button>Ver minhas notas</button></Link>
      </div>
    );
  }

  return (
    <div>
      <h2>{prova.titulo}</h2>
      {prova.descricao && <p>{prova.descricao}</p>}

      {prova.questoes.map((questao, indice) => (
        <div key={questao.id} className="cartao">
          <p><strong>{indice + 1}. {questao.enunciado}</strong></p>
          {questao.alternativas.map((alternativa) => (
            <label key={alternativa.id} className="alternativa" style={{ fontWeight: 400 }}>
              <input
                type="radio"
                name={`questao-${questao.id}`}
                checked={respostas[questao.id] === alternativa.id}
                onChange={() => escolher(questao.id, alternativa.id)}
                disabled={enviando}
              />
              {alternativa.texto}
            </label>
          ))}
        </div>
      ))}

      {erro && <div className="erro">{erro}</div>}

      {/* Barra fixa no fim da tela: tentativa | cronômetro | botão */}
      <div className="barra-prova">
        <span className="info">
          Tentativa {tentativas.realizadas + 1} de {tentativas.limite}
        </span>
        <span className={`cronometro ${restante !== null && restante <= 300 ? "alerta" : ""}`}>
          {restante === 0 ? "Tempo esgotado" : formatarTempo(restante ?? TEMPO_LIMITE)}
        </span>
        <button onClick={() => enviar()} disabled={enviando}>
          {enviando ? "Enviando..." : "Enviar respostas"}
        </button>
      </div>
    </div>
  );
}