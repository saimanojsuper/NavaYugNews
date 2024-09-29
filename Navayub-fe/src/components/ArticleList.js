import React from "react";

const ArticleList = ({ articles }) => {
  return (
    <div>
      {articles.length > 0 ? (
        articles.map((article) => (
          <a href={article.articleUrl} target="_blank">
          <div key={article.id} className="article">
            <h2>{article.headline}</h2>
            <p >{article.description}</p>
          </div>
          </a>
        ))
      ) : (
        <p>No articles found.</p>
      )}
    </div>
  );
};

export default ArticleList;
