import { useState } from "react";

export default function CreateCollectionForm({ CreateBookCollection, error }) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const data = await CreateBookCollection(name, description);

      setMessage(`Collection "${data.name}" created successfully !`);
      setName("");
      setDescription("");
    } catch (err) {
      setMessage(error);
      console.error(err);
    }
  };

  return (
    <div className="max-w-full mx-auto p-6 bg-gray-100 border border-gray-300 rounded-lg shadow-lg">
      <h2 className="text-2xl font-bold mb-6 text-gray-900">Create</h2>
      {message && <p className="mb-4 text-gray-800 font-medium">{message}</p>}
      <form onSubmit={handleSubmit} className="flex items-center gap-4">
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Name"
          className="flex-[1] border border-gray-400 rounded-lg px-3 py-2 bg-white text-gray-900 placeholder-gray-500 focus:ring-2 focus:ring-gray-400 focus:outline-none"
          required
        />

        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Description"
          className="flex-[2] border border-gray-400 rounded-lg px-3 py-2 h-[40px] resize-y bg-white text-gray-900 placeholder-gray-500 focus:ring-2 focus:ring-gray-400 focus:outline-none"
          required
        />

        <button
          type="submit"
          className="bg-gray-900 text-white font-semibold px-5 py-2 rounded-lg hover:bg-gray-700 transition-colors"
        >
          Créer
        </button>
      </form>
    </div>
  );
}
