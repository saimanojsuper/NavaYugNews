import React from "react";

const Search = ({ searchQuery, setSearchQuery, setSearchClick, setCurrentPage }) => {
  return (
    <div>
      <input
        type="text"
        placeholder="Search articles..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        style={{ marginLeft: '10px', padding: '10px 20px', fontSize: '16px', cursor: 'pointer' }}
      />
       <button
        onClick={()=>{
          setSearchClick(searchQuery)
          setCurrentPage(1)
        }}
        style={{ marginLeft: '10px', padding: '10px 20px', fontSize: '16px', cursor: 'pointer' }}
      >
        Search
      </button>
    </div>
  );
};


export default Search;
