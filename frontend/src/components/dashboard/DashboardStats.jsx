import React from "react";
import { Users, Dumbbell, TrendingUp } from "lucide-react";

const DashboardStats = ({ userCount, exerciseCount }) => {
  const stats = [
    {
      title: "Total Users",
      value: userCount,
      icon: Users,
      trendUp: true,
    },
    {
      title: "Total Exercises",
      value: exerciseCount,
      icon: Dumbbell,
      trendUp: true,
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
      {stats.map((stat, index) => (
        <div
          key={index}
          className="bg-white p-6 rounded-lg shadow-md flex justify-between items-center"
        >
          <div>
            <h3 className="text-sm font-medium text-gray-500">{stat.title}</h3>
            <p className="text-2xl font-bold">{stat.value}</p>
            <span
              className={`text-sm ${
                stat.trendUp ? "text-green-500" : "text-red-500"
              }`}
            >
              {stat.trend}
            </span>
          </div>
          <stat.icon className="w-8 h-8 text-gray-400" />
        </div>
      ))}
    </div>
  );
};

export default DashboardStats;
