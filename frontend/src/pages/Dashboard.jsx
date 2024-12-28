import React, { useEffect, useState } from "react";
import DashboardStats from "../components/dashboard/DashboardStats";
import { getUserCount } from "../services/userService";
import { getExerciseCount } from "../services/exerciseService";

const Dashboard = () => {
  const [userCount, setUserCount] = useState(0);
  const [exerciseCount, setExerciseCount] = useState(0);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const users = await getUserCount(); // Appelle la version filtrée
        const exercises = await getExerciseCount();
        setUserCount(users);
        setExerciseCount(exercises);
      } catch (error) {
        console.error("Erreur lors de la récupération des statistiques :", error);
      }
    };

    fetchStats();
  }, []);

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Dashboard</h1>
      <DashboardStats userCount={userCount} exerciseCount={exerciseCount} />
    </div>
  );
};

export default Dashboard;
