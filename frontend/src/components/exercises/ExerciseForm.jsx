import React, { useEffect, useState } from "react";
import {
  getAllExercises,
  addExercise,
  updateExercise,
  deleteExercise,
} from "../services/exerciseService";
import ExerciseForm from "./ExerciseForm"; // Import ExerciseForm

const Exercises = () => {
  const [exercises, setExercises] = useState([]);
  const [selectedExercise, setSelectedExercise] = useState(null);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [exerciseForm, setExerciseForm] = useState({
    name: "",
    type: "",
    minStressLevel: "",
    maxStressLevel: "",
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
        console.error("Erreur lors du chargement des exercices:", error);
        setError("Erreur lors du chargement des exercices");
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

  const uploadFile = async (file, type) => {
    if (!file) return null;

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch(`http://localhost:8080/api/exercises/upload/${type}`, {
        method: "POST",
        body: formData,
      });
      if (!response.ok) {
        console.error(`Erreur lors de l'upload de ${type}: ${response.statusText}`);
        return null;
      }
      const data = await response.json();
      return data.fileName;
    } catch (error) {
      console.error(`Erreur lors de l'upload du fichier (${type}):`, error);
      return null;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const imageFileName = selectedFiles.image
        ? await uploadFile(selectedFiles.image, "image")
        : exerciseForm.imageUrl;
      const videoFileName = selectedFiles.video
        ? await uploadFile(selectedFiles.video, "video")
        : exerciseForm.videoUrl;

      const exerciseData = {
        ...exerciseForm,
        imageUrl: imageFileName ? `/uploads/images/${imageFileName}` : exerciseForm.imageUrl,
        videoUrl: videoFileName ? `/uploads/videos/${videoFileName}` : exerciseForm.videoUrl,
      };

      if (isEditing) {
        const updatedExercise = await updateExercise(selectedExercise.id, exerciseData);
        setExercises((prev) =>
          prev.map((exercise) =>
            exercise.id === selectedExercise.id ? updatedExercise : exercise
          )
        );
      } else {
        const addedExercise = await addExercise(exerciseData);
        setExercises((prev) => [...prev, addedExercise]);
      }

      resetForm();
    } catch (error) {
      console.error("Erreur lors de la soumission:", error);
      setError(`Erreur lors de ${isEditing ? "la mise à jour" : "l'ajout"} de l'exercice`);
    }
  };

  const handleDeleteExercise = async (id) => {
    if (window.confirm("Êtes-vous sûr de vouloir supprimer cet exercice ?")) {
      try {
        await deleteExercise(id);
        setExercises((prev) => prev.filter((exercise) => exercise.id !== id));
        setSelectedExercise(null);
      } catch (error) {
        console.error("Erreur lors de la suppression de l'exercice:", error);
        setError("Erreur lors de la suppression de l'exercice");
      }
    }
  };

  const resetForm = () => {
    setExerciseForm({
      name: "",
      type: "",
      minStressLevel: "",
      maxStressLevel: "",
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
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="bg-white rounded-xl shadow-lg mb-8 p-6">
        <h1 className="text-3xl font-bold">Gestion des Exercices</h1>
        <p className="text-gray-600 mt-2">
          Gérez votre catalogue d'exercices en ajoutant, modifiant ou supprimant des exercices
        </p>
      </div>

      {error && (
        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
          <h3 className="text-sm font-medium text-red-800">Erreur</h3>
          <p className="text-sm text-red-700 mt-1">{error}</p>
        </div>
      )}

      {selectedExercise && !isEditing ? (
        <ExerciseDetails
          exercise={selectedExercise}
          onEdit={() => {
            setExerciseForm(selectedExercise);
            setIsEditing(true);
          }}
          onDelete={() => handleDeleteExercise(selectedExercise.id)}
          onBack={() => setSelectedExercise(null)}
        />
      ) : (
        <div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
            {exercises.map((exercise) => (
              <ExerciseCard
                key={exercise.id}
                exercise={exercise}
                onClick={() => setSelectedExercise(exercise)}
              />
            ))}
          </div>

          <ExerciseForm
            exerciseForm={exerciseForm}
            handleInputChange={handleInputChange}
            handleFileSelect={handleFileSelect}
            isEditing={isEditing}
            onSubmit={handleSubmit}
            onCancel={() => {
              resetForm();
              setIsEditing(false);
              setSelectedExercise(null);
            }}
          />
        </div>
      )}
    </div>
  );
};

export default Exercises;
