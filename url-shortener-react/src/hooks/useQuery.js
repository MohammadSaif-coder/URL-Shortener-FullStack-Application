//import { useQuery } from "react-query"
//import api from "../api/api"
//
//
//export const useFetchMyShortUrls = (token, onError) => {
//    return useQuery("my-shortenurls",
//         async () => {
//            return await api.get(
//                "/api/urls/myurls",
//            {
//                headers: {
//                    "Content-Type": "application/json",
//                    Accept: "application/json",
//                    Authorization: "Bearer " + token,
//                },
//            }
//        );
//    },
//          {
//            select: (data) => {
//                const sortedData = data.data.sort(
//                    (a, b) => new Date(b.createdDate) - new Date(a.createdDate)
//                );
//                return sortedData;
//            },
//            onError,
//            staleTime: 5000
//          }
//        );
//};
//
//export const useFetchTotalClicks = (token, onError) => {
//    return useQuery("url-totalclick",
//         async () => {
//            return await api.get(
//                "/api/urls/totalClicks?startDate=2024-01-01&endDate=2025-12-31",
//            {
//                headers: {
//                    "Content-Type": "application/json",
//                    Accept: "application/json",
//                    Authorization: "Bearer " + token,
//                },
//            }
//        );
//    },
//          {
//            select: (data) => {
//                // data.data =>
//                    //  {
//                    //     "2024-01-01": 120,
//                    //     "2024-01-02": 95,
//                    //     "2024-01-03": 110,
//                    //   };
//
//                const convertToArray = Object.keys(data.data).map((key) => ({
//                    clickDate: key,
//                    count: data.data[key], // data.data[2024-01-01]
//                }));
//                // Object.keys(data.data) => ["2024-01-01", "2024-01-02", "2024-01-03"]
//
//                // FINAL:
//                //   [
//                //     { clickDate: "2024-01-01", count: 120 },
//                //     { clickDate: "2024-01-02", count: 95 },
//                //     { clickDate: "2024-01-03", count: 110 },
//                //   ]
//                return convertToArray;
//            },
//            onError,
//            staleTime: 5000
//          }
//        );
//};

//import { useQuery } from "react-query";
//import api from "../api/api";
//
//// ================= MY SHORT URLS =================
//
//export const useFetchMyShortUrls = (token, onError) => {
//  return useQuery(
//    ["my-shortenurls", token],
//    async () => {
//      const response = await api.get("/api/urls/myurls", {
//        headers: {
//          "Content-Type": "application/json",
//          Accept: "application/json",
//          Authorization: "Bearer " + token,
//        },
//      });
//
//      return response.data;
//    },
//    {
//      enabled: !!token,
//      select: (data) => {
//        if (!data) return [];
//
//        return data.sort(
//          (a, b) => new Date(b.createdDate) - new Date(a.createdDate)
//        );
//      },
//      onError,
//      staleTime: 5000,
//    }
//  );
//};
//
//// ================= TOTAL CLICKS =================
//
//export const useFetchTotalClicks = (token, onError) => {
//  return useQuery(
//    ["url-totalclick", token],
//    async () => {
//      const startDate = new Date().getFullYear() + "-01-01";
//      const endDate = new Date().getFullYear() + "-12-31";
//      const response = await api.get(
//        `/api/urls/totalClicks?startDate=${startDate}&endDate=${endDate}`,
//        {
//          headers: {
//            "Content-Type": "application/json",
//            Accept: "application/json",
//            Authorization: "Bearer " + token,
//          },
//        }
//      );
//
//      return response.data;
//    },
//    {
//      enabled: !!token,
//      select: (data) => {
//        if (!data) return [];
//
//        return Object.keys(data).map((key) => ({
//          clickDate: key,
//          count: data[key],
//        }));
//      },
//      onError,
//      staleTime: 5000,
//    }
//  );
//};
import { useQuery } from "react-query";
import api from "../api/api";


// ================= MY SHORT URLS =================

export const useFetchMyShortUrls = (token, onError) => {
  return useQuery(
    ["my-shortenurls", token],   // ✅ unique key
    async () => {
      const response = await api.get("/api/urls/myurls", {
        headers: {
          Authorization: "Bearer " + token,
        },
      });

      return response.data;   // ✅ return only data
    },
    {
      enabled: !!token,       // ✅ prevents unauthorized call
      select: (data) => {
        if (!data) return [];

        return data.sort(
          (a, b) => new Date(b.createdDate) - new Date(a.createdDate)
        );
      },
      onError,
      staleTime: 5000,
    }
  );
};


// ================= TOTAL CLICKS =================

export const useFetchTotalClicks = (token, onError) => {
  return useQuery(
    ["url-totalclick", token],   // ✅ unique key
    async () => {

      const year = new Date().getFullYear();
      const startDate = `${year}-01-01`;
      const endDate = `${year}-12-31`;

      const response = await api.get(
        `/api/urls/totalClicks?startDate=${startDate}&endDate=${endDate}`,
        {
          headers: {
            Authorization: "Bearer " + token,
          },
        }
      );

      return response.data;   // ✅ return only JSON
    },
    {
      enabled: !!token,       // ✅ important
      select: (data) => {
        if (!data) return [];

        return Object.keys(data).map((key) => ({
          clickDate: key,
          count: data[key],
        }));
      },
      onError,
      staleTime: 5000,
    }
  );
};
