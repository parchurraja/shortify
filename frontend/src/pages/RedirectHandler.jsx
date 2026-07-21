import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';

const RedirectHandler = () => {
  const { shortCode } = useParams();

  useEffect(() => {
    const apiBase = import.meta.env.VITE_API_BASE_URL;
    let redirectUrl;
    
    if (apiBase) {
      redirectUrl = `${apiBase}/${shortCode}`;
    } else {
      // In local development, direct to the Spring Boot port (8080)
      const protocol = window.location.protocol;
      const hostname = window.location.hostname;
      redirectUrl = `${protocol}//${hostname}:8080/${shortCode}`;
    }
    
    window.location.replace(redirectUrl);
  }, [shortCode]);

  return (
    <div className="flex h-screen items-center justify-center bg-slate-950 text-indigo-400 font-semibold">
      <div className="flex flex-col items-center gap-4">
        <div className="h-10 w-10 border-4 border-indigo-500 border-t-transparent animate-spin rounded-full"></div>
        <span className="text-lg">Redirecting you to destination...</span>
      </div>
    </div>
  );
};

export default RedirectHandler;
