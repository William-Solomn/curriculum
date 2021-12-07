import React from 'react';
import ReactDOM from 'react-dom';

import TableList from "./components/table";
import 'antd/dist/antd.css';

const render=()=>{
    ReactDOM.render(
        <TableList/>,

        document.getElementById('root')
    );
}
render();

