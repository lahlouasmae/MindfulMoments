const API_BASE = '/api';
const EXERCISE_API_URL = `${API_BASE}/exercises`;

// Récupérer tous les exercices
export const getAllExercises = async () => {
  try {
    const response = await fetch(EXERCISE_API_URL, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans getAllExercises:", error);
    throw error;
  }
};

// Nombre d'exercices
export const getExerciseCount = async () => {
  try {
    const response = await fetch(`${EXERCISE_API_URL}/count`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans getExerciseCount:", error);
    throw error;
  }
};

// Récupérer un exercice par ID
export const getExerciseById = async (id) => {
  try {
    const response = await fetch(`${EXERCISE_API_URL}/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans getExerciseById:", error);
    throw error;
  }
};

// Ajouter un exercice
export const addExercise = async (exercise) => {
  try {
    const response = await fetch(EXERCISE_API_URL, {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(exercise)
    });
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans addExercise:", error);
    throw error;
  }
};

// Mettre à jour un exercice
export const updateExercise = async (id, exercise) => {
  try {
    const response = await fetch(`${EXERCISE_API_URL}/${id}`, {
      method: "PUT",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(exercise)
    });
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans updateExercise:", error);
    throw error;
  }
};

// Supprimer un exercice
export const deleteExercise = async (id) => {
  try {
    const response = await fetch(`${EXERCISE_API_URL}/${id}`, {
      method: "DELETE",
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
  } catch (error) {
    console.error("Erreur dans deleteExercise:", error);
    throw error;
  }
};

// Upload d'image
export const uploadImage = async (id, file) => {
  try {
    const formData = new FormData();
    formData.append("file", file);
    
    const response = await fetch(`${EXERCISE_API_URL}/${id}/uploadImage`, {
      method: "POST",
      body: formData
    });
    
    if (!response.ok) {
      throw new Error(`Erreur lors de l'upload de l'image: ${response.statusText}`);
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans uploadImage:", error);
    throw error;
  }
};

// Upload de vidéo
export const uploadVideo = async (id, file) => {
  try {
    const formData = new FormData();
    formData.append("file", file);
    
    const response = await fetch(`${EXERCISE_API_URL}/${id}/uploadVideo`, {
      method: "POST",
      body: formData
    });
    
    if (!response.ok) {
      throw new Error(`Erreur lors de l'upload de la vidéo: ${response.statusText}`);
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans uploadVideo:", error);
    throw error;
  }
};