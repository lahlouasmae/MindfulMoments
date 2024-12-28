import React, { useEffect, useState } from "react";
import { getAllUsers } from "../../services/userService";

const UserList = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      const data = await getAllUsers();
      setUsers(data);
    };
    fetchUsers();
  }, []);

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Liste des Utilisateurs</h2>
      <table className="w-full bg-white border">
        <thead>
          <tr>
            <th className="py-2 px-4 text-left">Nom</th>
            <th className="py-2 px-4 text-left">Email</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td className="py-2 px-4">{user.firstName} {user.lastName}</td>
              <td className="py-2 px-4">{user.email}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserList;
