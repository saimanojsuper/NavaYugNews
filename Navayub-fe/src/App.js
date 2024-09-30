import "./styles.css";
import React, { useEffect, useState } from "react";
import ArticleList from "./components/ArticleList";
import Search from "./components/Search";
import Pagination from "./components/Pagination";
import callAPI from "./api/callAPI"

const App = () => {

  var data = require('./data/sampledata.json');
  var data2 = require('./data/sampledata-1.json');
  console.log('data-json',data.articleDataList);

  const [searchQuery, setSearchQuery] = useState(null);
  const [searchClick, setSearchClick] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [articlesPerPage] = useState(10);

  const [articlesData, setArticlesData] = useState({
    "metaData": {
      "totalCount": 0
    },
    "articleDataList": []
  });

  // Search functionality
  // const filteredArticles = articles.filter((article) =>
  //   article.headline.toLowerCase().includes(searchQuery.toLowerCase())
  // );

  // Pagination logic
  // const indexOfLastArticle = currentPage * articlesPerPage;
  // const indexOfFirstArticle = indexOfLastArticle - articlesPerPage;
  // const currentArticles = articles.slice(
  //   indexOfFirstArticle,
  //   indexOfLastArticle
  // );

  useEffect(
    ()=> {

      callAPI({
        pageNumber: currentPage,
        pageSize: articlesPerPage,
        setArticlesData: setArticlesData,
        searchTerm: searchQuery
      })

      // console.log('current page', currentPage, 'articles', articlesData)
      // if(currentPage != 1){
      // setArticles(data2.articleDataList)
      // }
    },
    [currentPage, searchClick]
  )

  return (
    <div className="App">
       <div className="center-container">
       <div className="center-content">
      <h1>NavaYug News Paper App</h1>
      <Search searchQuery={searchQuery} setSearchClick={setSearchClick} setSearchQuery={setSearchQuery} />
      </div>
      </div>
      <ArticleList articles={articlesData.articleDataList} />
      <Pagination
        totalArticles={articlesData.metaData.totalCount}
        articlesPerPage={articlesPerPage}
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
      />
    </div>
  );
};

export default App;
