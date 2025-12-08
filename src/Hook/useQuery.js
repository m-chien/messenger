import React, { useState } from "react";

const useQuery = (initial) => {
  const [query, setquery] = useState(initial);
  const updateQuery = (newQuery) => {
    setquery((prev) => ({
      ...prev,
      ...newQuery,
    }));
  };
  const resetQuery = () => {
    setquery(initial);
  };

  return [query, updateQuery, resetQuery];
};

export default useQuery;
