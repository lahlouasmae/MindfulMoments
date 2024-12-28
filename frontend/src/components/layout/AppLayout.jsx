import React, { useState } from "react";
import { Link } from "react-router-dom";
import { LayoutDashboard, Users, Dumbbell, LogOut } from "lucide-react";

const AppLayout = ({ children }) => {
  const [isSidebarOpen, setSidebarOpen] = useState(true);

  return (
    <div className="flex min-h-screen bg-gray-100">
      {/* Sidebar */}
      <div 
        className={`${
          isSidebarOpen ? "w-64" : "w-20"
        } bg-blue-600 text-white flex flex-col flex-shrink-0`}
      >
        <div className="p-4">
          <h1 className={`font-bold text-xl ${!isSidebarOpen && "hidden"}`}>
            Mindful Moments
          </h1>
        </div>
        <nav className="p-4 flex-grow">
          <Link to="/dashboard" className="flex items-center p-3 hover:bg-blue-700 rounded">
            <LayoutDashboard className="w-6 h-6" />
            {isSidebarOpen && <span className="ml-3">Dashboard</span>}
          </Link>
          <Link to="/users" className="flex items-center p-3 hover:bg-blue-700 rounded">
            <Users className="w-6 h-6" />
            {isSidebarOpen && <span className="ml-3">Users</span>}
          </Link>
          <Link to="/exercises" className="flex items-center p-3 hover:bg-blue-700 rounded">
            <Dumbbell className="w-6 h-6" />
            {isSidebarOpen && <span className="ml-3">Exercises</span>}
          </Link>
        </nav>
        <div className="p-4 border-t mt-auto">
          <div className="flex items-center p-3 hover:bg-red-700 rounded cursor-pointer">
            <LogOut className="w-6 h-6 text-red-300" />
            {isSidebarOpen && <span className="ml-3">Log out</span>}
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col">
        <header className="p-4 bg-white shadow">
          <button 
            onClick={() => setSidebarOpen(!isSidebarOpen)} 
            className="text-gray-700"
          >
            ☰
          </button>
        </header>
        <main className="p-4 flex-grow">{children}</main>
      </div>
    </div>
  );
};

export default AppLayout;