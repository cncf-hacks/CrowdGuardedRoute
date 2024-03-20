import * as dotenv from 'dotenv';

module.exports = ({ config }) => {
  return {
    ...config,
    android: {
      ...config.android,
      config: {
        googleMaps: {
          apiKey: process.env.EXPO_PUBLIC_API_KEY,
        },
      },
      googleServicesFile: process.env.GOOGLE_SERVICES_JSON,
    },
    extra: {
      ...config.extra,
      backend: process.env.BACKEND_DOMAIN,
    },
  };
};
