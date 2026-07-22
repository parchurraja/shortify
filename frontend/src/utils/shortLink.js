export const getShortLinkBaseUrl = () => {
  const configuredBase = import.meta.env.VITE_SHORT_LINK_BASE_URL || import.meta.env.VITE_API_BASE_URL;

  if (!configuredBase) {
    return window.location.origin;
  }

  const trimmedBase = configuredBase.trim();

  if (!trimmedBase) {
    return window.location.origin;
  }

  if (trimmedBase.startsWith('http://') || trimmedBase.startsWith('https://')) {
    try {
      const url = new URL(trimmedBase);
      const pathname = url.pathname.replace(/\/api\/?$/, '').replace(/\/$/, '');
      return `${url.origin}${pathname || ''}`;
    } catch {
      return trimmedBase.replace(/\/api\/?$/, '').replace(/\/$/, '');
    }
  }

  return trimmedBase.replace(/\/api\/?$/, '').replace(/\/$/, '') || '/';
};

export const getShortLinkUrl = (shortCode, shortUrl) => {
  if (shortUrl) {
    return shortUrl;
  }

  const baseUrl = getShortLinkBaseUrl();
  const normalizedBase = baseUrl === '/' ? '' : baseUrl;
  return `${normalizedBase}/${shortCode}`;
};

export const getShortLinkDisplayBase = () => {
  const baseUrl = getShortLinkBaseUrl();

  if (!baseUrl || baseUrl === '/') {
    return window.location.host;
  }

  try {
    return new URL(baseUrl).host;
  } catch {
    return baseUrl.replace(/^https?:\/\//, '').replace(/\/$/, '');
  }
};
