import axios from 'axios';
import { useState } from 'react';

function App() {
  const [tags, setTags] = useState<string>('');
  const [tagInput, setTagInput] = useState('');
  const [file, setFile] = useState(null);
  const [tagSearchInput, setTagSearchInput] = useState('');
  const [tagSearchInputResult, setTagSearchInputResult] = useState('');
  const REACT_APP_APP_BASE_URL = 'http://localhost:8080';

  function addTag() {
    setTags(tagInput);
    setTagInput('');
  }

  function handleFIleInputChange(event: React.ChangeEvent<HTMLInputElement>) {
    if (event.target.files === null) return;
    setFile(event.target.files[0]);
  }

  function handleFileSubmit() {
    const formData = new FormData();
    formData.append('file', file);
    axios.post(REACT_APP_APP_BASE_URL + '/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      params: { department: tagInput },
    });
  }

  function fetch() {
    axios
      .get(REACT_APP_APP_BASE_URL + '/documentByTag', {
        params: { tag: tagSearchInput },
      })
      .then((e) => setTagSearchInputResult(e.data));
  }

  return (
    <>
      <h2>Upload documents here and tag them</h2>
      <label htmlFor='fileupload'>Upload file</label>
      <br />
      <input
        type='file'
        name='file'
        id='fileupload'
        onChange={handleFIleInputChange}
      />
      <br />
      <b>{tags}</b>
      <br />
      <input
        type='text'
        name='tag'
        id='tag'
        placeholder='Add Tag'
        value={tagInput}
        onChange={(e) => setTagInput(e.target.value)}
      />
      <button type='button' onClick={addTag} disabled={tagInput === ''}>
        +
      </button>
      <br />
      <br />
      <button type='button' onClick={handleFileSubmit}>
        Submit
      </button>
      <hr />
      <h2>Get all documents by tag</h2>
      <input
        type='text'
        name='tag'
        id='tag'
        placeholder='Enter Tag'
        value={tagSearchInput}
        onChange={(e) => setTagSearchInput(e.target.value)}
      />
      <button onClick={fetch}>Fetch</button>
      {JSON.stringify(tagSearchInputResult)}
    </>
  );
}

export default App;
