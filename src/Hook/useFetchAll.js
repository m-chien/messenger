import { useEffect, useState } from "react";
import { api } from "../Api/Api";

const useFetchAll = (path, config = {}) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAPI = async () => {
      try {
        const res = await api.get(path, config);
        setData(res.data);
      } catch (e) {
        console.error("Fetch ALL error:", e);
      } finally {
        setLoading(false);
      }
    };

    fetchAPI();
  }, [path, JSON.stringify(config)]);

  return { data, loading };
};

export default useFetchAll;
