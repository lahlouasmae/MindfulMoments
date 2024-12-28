const API_BASE = '/api';

// Service pour les utilisateurs
export const getAllUsers = async () => {
  try {
    const response = await fetch(`${API_BASE}/users`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error("Erreur lors de la récupération des utilisateurs");
    }
    return response.json();
  } catch (error) {
    console.error("Erreur dans getAllUsers:", error);
    return [];
  }
};

// Nombre d'utilisateurs (excluant admin)
export const getUserCount = async () => {
  try {
    const users = await getAllUsers();
    const nonAdminUsers = users.filter(user => user.email !== "admin@gmail.com");
    return nonAdminUsers.length;
  } catch (error) {
    console.error("Erreur dans getUserCount:", error);
    return 0;
  }
};

// Suppression d'un utilisateur
export const deleteUserById = async (id) => {
  try {
    const response = await fetch(`${API_BASE}/users/${id}`, {
      method: "DELETE",
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error("Erreur lors de la suppression de l'utilisateur");
    }
  } catch (error) {
    console.error("Erreur dans deleteUserById:", error);
    throw error;
  }
};