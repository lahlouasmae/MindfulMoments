/*import React from "react";

const ExerciseList = ({ exercises, onSelect, onDelete }) => {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
      {exercises.map((exercise) => (
        <div
          key={exercise.id}
          className="p-6 bg-white rounded-lg shadow-lg hover:shadow-2xl transform transition duration-300 hover:scale-105 cursor-pointer"
        >
          <img
            src={
              exercise.imageUrl
                ? `http://localhost:8080/uploads/images/${exercise.imageUrl}`
                : "https://via.placeholder.com/150"
            }
            alt={exercise.name}
            className="w-24 h-24 rounded-full mx-auto mb-4 border-4 border-blue-200 object-cover"
          />
          <h2
            className="text-center text-xl font-semibold text-gray-700"
            onClick={() => onSelect(exercise)}
          >
            {exercise.name}
          </h2>
          <p className="text-center text-sm text-gray-500">{exercise.type}</p>
          <button
            onClick={() => onDelete(exercise.id)}
            className="mt-4 w-full px-4 py-2 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition"
          >
            Supprimer
          </button>
        </div>
      ))}
    </div>
  );
};

export default ExerciseList;
*/