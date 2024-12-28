import React, { useEffect, useState } from "react";
import {
  getAllExercises,
  addExercise,
  updateExercise,
  deleteExercise,
  uploadImage,
  uploadVideo
} from "../services/exerciseService";

const ExerciseForm = ({ 
    exerciseForm, 
    handleInputChange, 
    handleFileSelect, 
    isEditing, 
    onSubmit, 
    onCancel 
}) => {
  const difficulties = ["Easy", "Moderate", "Hard"];

  return (
    <form
      onSubmit={onSubmit}
      method="POST"
      encType="multipart/form-data"
      className="space-y-8 bg-gradient-to-r from-gray-50 to-white p-8 rounded-xl shadow-lg"
    >
      <h2 className="text-2xl font-bold text-gray-800">
        {isEditing ? "Modify exercise" : "Add exercise"}
      </h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <span role="img" aria-label="icon">🏋️‍♂️</span> Exercise name
          </label>
          <input
            name="name"
            value={exerciseForm.name}
            onChange={handleInputChange}
            placeholder="Exercise name"
            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <span role="img" aria-label="icon">📋</span> Type
          </label>
          <input
            name="type"
            value={exerciseForm.type}
            onChange={handleInputChange}
            placeholder="Exercise type"
            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <span role="img" aria-label="icon">📊</span> Minimum stress level
          </label>
          <input
            type="range"
            name="minStressLevel"
            value={exerciseForm.minStressLevel}
            onChange={handleInputChange}
            min="0"
            max="100"
            className="w-full"
          />
          <p className="text-gray-600 mt-1">Current value: {exerciseForm.minStressLevel}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <span role="img" aria-label="icon">📊</span> Maximum stress level
          </label>
          <input
            type="range"
            name="maxStressLevel"
            value={exerciseForm.maxStressLevel}
            onChange={handleInputChange}
            min="0"
            max="100"
            className="w-full"
          />
          <p className="text-gray-600 mt-1">Current value: {exerciseForm.maxStressLevel}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <span role="img" aria-label="icon">⏳</span> Duration (minutes)
          </label>
          <input
            type="number"
            name="duration"
            value={exerciseForm.duration}
            onChange={handleInputChange}
            min="1"
            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <span role="img" aria-label="icon">🔥</span> Calories burned
          </label>
          <input
            type="number"
            name="calories"
            value={exerciseForm.calories}
            onChange={handleInputChange}
            min="0"
            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <span role="img" aria-label="icon">🎯</span> Difficulty
          </label>
          <select
            name="difficulty"
            value={exerciseForm.difficulty}
            onChange={handleInputChange}
            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm"
          >
            <option value="">Select difficulty</option>
            {difficulties.map((difficulty) => (
              <option key={difficulty} value={difficulty}>
                {difficulty}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="space-y-4">
        {[
          { label: "Detailed description", name: "description", placeholder: "Exercise description" },
          { label: "Step-by-step instructions", name: "instructions", placeholder: "Exercise instructions" },
          { label: "Benefits", name: "benefits", placeholder: "Exercise benefits" },
        ].map((field, index) => (
          <div key={index}>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {field.label}
            </label>
            <textarea
              name={field.name}
              value={exerciseForm[field.name]}
              onChange={handleInputChange}
              placeholder={field.placeholder}
              className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm min-h-[80px]"
            />
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {[
          { label: "Image", name: "image", accept: "image/*" },
          { label: "Video", name: "video", accept: "video/*" },
        ].map((fileField, index) => (
          <div key={index}>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {fileField.label}
            </label>
            <input
              type="file"
              name={fileField.name}
              accept={fileField.accept}
              onChange={handleFileSelect}
              className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm"
            />
          </div>
        ))}
      </div>

      <div className="flex justify-end gap-4">
        <button
          type="submit"
          className="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-all shadow-md"
        >
          {isEditing ? "Update" : "Add"}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-all shadow-md"
        >
          Cancel
        </button>
      </div>
    </form>
  );
};

const ExerciseCard = ({ exercise, onClick }) => (
  <div 
    className="bg-white rounded-xl shadow-lg overflow-hidden cursor-pointer transform transition-transform hover:scale-105"
    onClick={onClick}
  >
    <div className="p-0">
      {exercise.imageUrl && exercise.imageUrl.startsWith("/uploads/images") ? (
        <img
          src={`http://localhost:8080${exercise.imageUrl}`}
          alt={exercise.name}
          className="w-full h-48 object-cover rounded-lg mb-6"
        />
      ) : (
        <img
          src="/api/placeholder/400/320"
          alt="placeholder"
          className="w-full h-48 object-cover rounded-lg mb-6"
        />
      )}

      <div className="p-4">
        <h3 className="text-xl font-semibold mb-2">{exercise.name}</h3>
        <p className="text-gray-600">{exercise.type}</p>
        <div className="flex items-center gap-2 mt-2">
          <span className="text-sm bg-blue-100 text-blue-800 px-2 py-1 rounded">
            {exercise.difficulty}
          </span>
          <span className="text-sm bg-gray-100 text-gray-800 px-2 py-1 rounded">
            {exercise.duration} min
          </span>
        </div>
      </div>
    </div>
  </div>
);

const ExerciseDetails = ({ exercise, onEdit, onDelete, onBack }) => (
  <div className="bg-gradient-to-br from-blue-50 to-white shadow-2xl rounded-3xl max-w-4xl mx-auto p-8 space-y-8">
    <h2 className="text-4xl font-extrabold text-gray-800 mb-4 text-center">
      {exercise.name}
    </h2>

    <div className="flex flex-col lg:flex-row gap-8 items-center">
      {exercise.imageUrl && (
        <img
          src={exercise.imageUrl.startsWith("/uploads/images") ? `http://localhost:8080${exercise.imageUrl}` : "/api/placeholder/400/320"}
          alt={exercise.name}
          className="w-full lg:w-1/2 h-60 object-cover rounded-2xl shadow-lg border-4 border-blue-100"
        />
      )}
      {exercise.videoUrl && (
        <video
          src={`http://localhost:8080${exercise.videoUrl}`}
          controls
          className="w-full lg:w-1/2 rounded-2xl shadow-lg border-4 border-blue-100"
        />
      )}
    </div>

    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 border-t pt-6">
      {[
        { label: "Type", value: exercise.type },
        { label: "Duration", value: `${exercise.duration} minutes` },
        { label: "Calories", value: `${exercise.calories} kcal` },
        { label: "Difficulty", value: exercise.difficulty },
      ].map((info, index) => (
        <div key={index} className="bg-gradient-to-r from-blue-100 to-blue-50 p-4 rounded-lg shadow-md">
          <h3 className="text-lg font-bold text-gray-700">{info.label}</h3>
          <p className="text-gray-800 mt-1 text-lg font-semibold">
            {info.label === "Difficulty" ? (
              <span className={`px-3 py-1 rounded-full ${
                info.value === "Easy" ? "bg-green-200 text-green-800" :
                info.value === "Moderate" ? "bg-yellow-200 text-yellow-800" :
                "bg-red-200 text-red-800"
              }`}>
                {info.value}
              </span>
            ) : info.value}
          </p>
        </div>
      ))}
    </div>

    <div className="space-y-6 border-t pt-6">
      {[
        { title: "Description", content: exercise.description },
        { title: "Instructions", content: exercise.instructions },
        { title: "Benefits", content: exercise.benefits },
      ].map((section, index) => (
        <div key={index} className="bg-white p-6 rounded-xl shadow-sm">
          <h3 className="text-lg font-bold text-gray-800 border-b pb-2">
            {section.title}
          </h3>
          <p className="mt-3 text-gray-700 text-base leading-relaxed">
            {section.content}
          </p>
        </div>
      ))}
    </div>

    <div className="flex justify-end gap-6 mt-8">
      <button
        onClick={onEdit}
        className="px-6 py-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 transition-all shadow-lg"
      >
        Edit
      </button>
      <button
        onClick={onDelete}
        className="px-6 py-2 bg-red-500 text-white rounded-full hover:bg-red-600 transition-all shadow-lg"
      >
        Delete
      </button>
      <button
        onClick={onBack}
        className="px-6 py-2 bg-gray-500 text-white rounded-full hover:bg-gray-600 transition-all shadow-lg"
      >
        Back
      </button>
    </div>
  </div>
);

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <div className="flex justify-center items-center space-x-2 mt-6">
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
        className={`px-4 py-2 rounded-lg ${
          currentPage === 1
            ? "bg-gray-200 text-gray-500 cursor-not-allowed"
            : "bg-blue-500 text-white hover:bg-blue-600"
        }`}
      >
        Previous
      </button>
      
      {pages.map(page => (
        <button
          key={page}
          onClick={() => onPageChange(page)}
          className={`px-4 py-2 rounded-lg ${
            currentPage === page
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
        >
          {page}
        </button>
      ))}
      
      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
        className={`px-4 py-2 rounded-lg ${
          currentPage === totalPages
            ? "bg-gray-200 text-gray-500 cursor-not-allowed"
            : "bg-blue-500 text-white hover:bg-blue-600"
        }`}
      >
        Next
      </button>
    </div>
  );
};

const Exercises = () => {
  const [exercises, setExercises] = useState([]);
  const [selectedExercise, setSelectedExercise] = useState(null);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [exercisesPerPage] = useState(6);
  const [exerciseForm, setExerciseForm] = useState({
    name: "",
    type: "",
    minStressLevel: 0,
    maxStressLevel: 0,
    duration: "",
    calories: "",
    difficulty: "",
    description: "",
    instructions: "",
    benefits: "",
    imageUrl: "",
    videoUrl: "",
  });
  const [selectedFiles, setSelectedFiles] = useState({
    image: null,
    video: null,
  });

  useEffect(() => {
    const fetchExercises = async () => {
      try {
        const allExercises = await getAllExercises();
        setExercises(allExercises);
      } catch (error) {
        console.error("Error loading exercises:", error);
        setError("Error loading exercises");
      }
    };
    fetchExercises();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setExerciseForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileSelect = (e) => {
    const { name, files } = e.target;
    if (files && files[0]) {
      setSelectedFiles((prev) => ({
        ...prev,
        [name]: files[0],
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      let savedExercise;
      const formData = { ...exerciseForm };
      
      if (isEditing && selectedExercise) {
        savedExercise = await updateExercise(selectedExercise.id, formData);
      } else {
        savedExercise = await addExercise(formData);
      }

      if (selectedFiles.image) {
        try {
          await uploadImage(savedExercise.id, selectedFiles.image);
        } catch (error) {
          console.error("Error uploading image:", error);
        }
      }

      if (selectedFiles.video) {
        try {
          await uploadVideo(savedExercise.id, selectedFiles.video);
        } catch (error) {
          console.error("Error uploading video:", error);
        }
      }

      const allExercises = await getAllExercises();
      setExercises(allExercises);
      resetForm();
    } catch (error) {
      console.error("Error during submission:", error);
      setError(`Error ${isEditing ? "updating" : "adding"} exercise`);
    }
  };

  const handleDeleteExercise = async (id) => {
    if (window.confirm("Are you sure you want to delete this exercise?")) {
      try {
        await deleteExercise(id);
        setExercises((prev) => prev.filter((exercise) => exercise.id !== id));
        setSelectedExercise(null);
      } catch (error) {
        console.error("Error deleting exercise:", error);
        setError("Error deleting exercise");
      }
    }
  };

  const resetForm = () => {
    setExerciseForm({
      name: "",
      type: "",
      minStressLevel: 0,
      maxStressLevel: 0,
      duration: "",
      calories: "",
      difficulty: "",
      description: "",
      instructions: "",
      benefits: "",
      imageUrl: "",
      videoUrl: "",
    });
    setSelectedFiles({
      image: null,
      video: null,
    });
    setError(null);
    setShowForm(false);
    setIsEditing(false);
    setSelectedExercise(null);
  };

  // Pagination logic
  const indexOfLastExercise = currentPage * exercisesPerPage;
  const indexOfFirstExercise = indexOfLastExercise - exercisesPerPage;
  const currentExercises = exercises.slice(indexOfFirstExercise, indexOfLastExercise);
  const totalPages = Math.ceil(exercises.length / exercisesPerPage);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      {error && (
        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
          <div className="flex">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <p className="text-sm text-red-700">{error}</p>
            </div>
          </div>
        </div>
      )}

      {!showForm && !selectedExercise && (
        <div className="flex justify-end items-center mb-6">
          <button
            onClick={() => {
              setShowForm(true);
              setIsEditing(false);
              setExerciseForm({
                name: "",
                type: "",
                minStressLevel: 0,
                maxStressLevel: 0,
                duration: "",
                calories: "",
                difficulty: "",
                description: "",
                instructions: "",
                benefits: "",
                imageUrl: "",
                videoUrl: "",
              });
            }}
            className="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-all shadow-md flex items-center gap-2"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
            </svg>
            Add exercise
          </button>
        </div>
      )}

      {selectedExercise && !showForm ? (
        <ExerciseDetails
          exercise={selectedExercise}
          onEdit={() => {
            setExerciseForm({
              ...selectedExercise,
              minStressLevel: selectedExercise.minStressLevel || 0,
              maxStressLevel: selectedExercise.maxStressLevel || 0,
            });
            setIsEditing(true);
            setShowForm(true);
          }}
          onDelete={() => handleDeleteExercise(selectedExercise.id)}
          onBack={() => setSelectedExercise(null)}
        />
      ) : showForm ? (
        <ExerciseForm
          exerciseForm={exerciseForm}
          handleInputChange={handleInputChange}
          handleFileSelect={handleFileSelect}
          isEditing={isEditing}
          onSubmit={handleSubmit}
          onCancel={resetForm}
        />
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {currentExercises.map((exercise) => (
              <ExerciseCard
                key={exercise.id}
                exercise={exercise}
                onClick={() => setSelectedExercise(exercise)}
              />
            ))}
          </div>
          
          {exercises.length > exercisesPerPage && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          )}
        </>
      )}
    </div>
  );
};

export default Exercises;