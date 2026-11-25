import { Navigate } from "react-router-dom";

export default function RotaPrivada({ children, perfil }) {
  const token = localStorage.getItem("token");
  const perfilUsuario = localStorage.getItem("perfil");

  if (!token) {
    return <Navigate to="/login" replace />;
  }
  if (perfil && perfilUsuario !== perfil) {
    return <Navigate to="/provas" replace />;
  }
  return children;
}