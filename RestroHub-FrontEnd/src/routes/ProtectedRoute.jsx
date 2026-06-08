import { Navigate } from "react-router-dom";
import { getAccessToken } from "@services/common/authStorage";

const ProtectedRoute = ({ children }) => {
  const accessToken = getAccessToken();

  if (!accessToken) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
