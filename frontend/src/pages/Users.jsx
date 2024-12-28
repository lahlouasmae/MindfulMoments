import React, { useEffect, useState } from "react";
import { getAllUsers, deleteUserById } from "../services/userService";

const Users = () => {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const allUsers = await getAllUsers();
        const nonAdminUsers = allUsers.filter(
          (user) => user.email !== "admin@gmail.com"
        );
        setUsers(nonAdminUsers);
      } catch (error) {
        console.error("Erreur lors de la récupération des utilisateurs :", error);
      }
    };

    fetchUsers();
  }, []);

  // Supprimer un utilisateur
  const handleDeleteUser = async (userId) => {
    if (window.confirm("Are you sure you want to delete this user ?")) {
      try {
        await deleteUserById(userId); // Appeler la méthode de suppression
        setUsers((prevUsers) => prevUsers.filter((user) => user.id !== userId)); // Mettre à jour l'état
        alert("Utilisateur supprimé avec succès.");
      } catch (error) {
        console.error("Erreur lors de la suppression de l'utilisateur :", error);
        alert("Une erreur s'est produite lors de la suppression de l'utilisateur.");
      }
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-4xl font-extrabold mb-6 text-gray-800">
        Users Management
      </h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        {users.map((user) => (
          <div
            key={user.id}
            className="p-6 bg-white rounded-lg shadow-lg hover:shadow-2xl transform transition duration-300 hover:scale-105 cursor-pointer"
          >
            {/* Image */}
    
            <img
  src={
    user.imageUrl && user.imageUrl.startsWith("/uploads/users")
      ? `http://localhost:8080${user.imageUrl}`
      : "https://via.placeholder.com/150"
  }
  alt={`${user.firstName} ${user.lastName}`}
  className="w-24 h-24 rounded-full mx-auto mb-4 border-4 border-blue-200 object-cover"
/>



            {/* Nom */}
            <h2 className="text-center text-xl font-semibold text-gray-700">
              {user.firstName || "FirstName"} {user.lastName || "LastName"}
            </h2>
            {/* Boutons */}
            <div className="flex justify-center mt-4 space-x-2">
              <button
                className="px-4 py-2 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition"
                onClick={() => handleDeleteUser(user.id)}
              >
                Delete
              </button>
              <button
                className="px-4 py-2 bg-blue-500 text-white font-semibold rounded-lg hover:bg-blue-600 transition"
                onClick={() => setSelectedUser(user)}
              >
                See details
              </button>
            </div>
          </div>
        ))}
      </div>

      {selectedUser && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
            <h2 className="text-2xl font-bold mb-4 text-gray-800 text-center">
              User details
            </h2>
            <div className="flex flex-col items-center">
            <img
  src={
    selectedUser.imageUrl && selectedUser.imageUrl.startsWith("/uploads/users")
      ? `http://localhost:8080${selectedUser.imageUrl}`
      : "https://via.placeholder.com/150"
  }
  alt={`${selectedUser.firstName} ${selectedUser.lastName}`}
  className="w-24 h-24 rounded-full mx-auto mb-4 border-4 border-blue-200 object-cover"
/>

              <p className="text-lg mb-2">
                <strong>Full Name :</strong> {selectedUser.firstName}{" "}
                {selectedUser.lastName}
              </p>
              <p className="text-lg mb-2">
                <strong>Email :</strong> {selectedUser.email}
              </p>
              <p className="text-lg mb-2">
                <strong>Phone Number :</strong>{" "}
                {selectedUser.phoneNumber || "Non renseigné"}
              </p>
              <p className="text-lg">
                <strong>Adress :</strong>{" "}
                {selectedUser.address || "Non renseignée"}
              </p>
            </div>
            <button
              className="mt-4 w-full px-4 py-2 bg-blue-500 text-white font-semibold rounded-lg hover:bg-blue-600 transition"
              onClick={() => setSelectedUser(null)}
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Users;
