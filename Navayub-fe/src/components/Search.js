import React from "react";

const Search = ({ searchQuery, setSearchQuery, setSearchClick }) => {
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
        onClick={()=>{setSearchClick(searchQuery)}}
        style={{ marginLeft: '10px', padding: '10px 20px', fontSize: '16px', cursor: 'pointer' }}
      >
        Search
      </button>
    </div>
  );
};


export default Search;
