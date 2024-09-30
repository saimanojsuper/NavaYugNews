import React from "react";
import './ArticleGrid.css'; 

const ArticleList = ({ articles }) => {
  return (
    <div className="article-grid-container">
      <div className="grid">
      {articles.length > 0 ? (
        articles.map((article) => (

          <div className="article-card" key={article.id}>
          <a href={article.articleUrl} target="_blank">

          <h2 className="headline">{article.headline}</h2>
            <p className="description">{article.description}</p>
            <p className="meta">
              <span className="publishedTime">Published: {article.publishedTime}</span> | 
              <span className="genre"> Genre: {article.genre}</span>
            </p>
    
          </a>
          </div>
        ))
      ) : (
        <p>No articles found.</p>
      )}
    </div>
    </div>
  );
};

export default ArticleList;
