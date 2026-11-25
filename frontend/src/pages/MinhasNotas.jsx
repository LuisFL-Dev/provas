import { useEffect, useState } from "react";
import api, { mensagemDeErro } from "../api/axios";

export default function MinhasNotas() {
  const [tentativas, setTentativas] = useState([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    api.get("/tentativas/minhas")
      .then(({ data }) => setTentativas(data))
      .catch((error) => setErro(mensagemDeErro(error)));
  }, []);

  return (
    <div>
      <h2>Minhas notas</h2>
      {erro && <div className="erro">{erro}</div>}
      {tentativas.length === 0 && !erro && <p>Você ainda não fez nenhuma prova.</p>}

      {tentativas.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Prova</th>
              <th>Acertos</th>
              <th>Nota</th>
              <th>Data</th>
            </tr>
          </thead>
          <tbody>
            {tentativas.map((t) => (
              <tr key={t.id}>
                <td>{t.prova}</td>
                <td>{t.acertos} de {t.total}</td>
                <td>{Number(t.nota).toFixed(2).replace(".", ",")}</td>
                <td>{new Date(t.realizadaEm).toLocaleString("pt-BR")}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}