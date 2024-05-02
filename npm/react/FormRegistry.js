/** @typedef {{ getValue: () => string, reset: () => void, validate?: () => boolean }} RegistryFunctions */
/** @type {Record<string, RegistryFunctions>} */
const registeredInputs = {};

if (typeof window !== 'undefined' && typeof window.registerSnowMailInput === 'undefined') {
  window.registerSnowMailInput = (/** @type {string} */ name,
    /** @type {RegistryFunctions} */ r) => {
    registeredInputs[name] = r;
  };
}

/**
 * @returns {boolean}
 */
export function validateAll() {
  // eslint-disable-next-line no-restricted-syntax
  for (const name in registeredInputs) {
    if (Object.hasOwnProperty.call(registeredInputs, name)) {
      if (typeof registeredInputs[name].validate === 'function') {
        const valid = registeredInputs[name].validate();
        if (!valid) return false;
      }
    }
  }

  return true;
}

export function getAll() {
  const values = {};

  Object.keys(registeredInputs).forEach((name) => {
    values[name] = registeredInputs[name].getValue();
  });

  return values;
}

export function resetAll() {
  Object.keys(registeredInputs).forEach((name) => {
    registeredInputs[name].reset();
  });
}
