import { Table, Tag, Space,Modal,Button,Form,Input} from 'antd';
import 'antd/dist/antd.css';
import './index.css'
import {useState,useEffect } from "react";
import axios from 'axios';

axios.defaults.baseURL='http://47.100.49.178:8888/user'

const TableList = () =>{

    // eslint-disable-next-line no-undef
    const [visible,setVisable]=useState(false)

    const [result,setResult] = useState([]);
    const showModal = () => {
        setVisable(true)
    }
    const onCancelView = () => {
        setVisable(false)
    }
    const onOkView = () => {

        setTimeout(() => {
            setVisable(false);

        }, 2000);
    }
    useEffect(async ()=>{
        setTimeout(() => {
            axios.get('/getAllUser').then(res=>{
                setResult(res.data)
                console.log(res.data)
            })

        }, 5000);

    })
    const ModalView = () =>{
        return(
            <>
                <Modal

                    title="编辑信息"
                    visible={visible}
                    onOk={onOkView}

                    onCancel={onCancelView}
                >
                    <Form
                        name="modalForm"
                    >
                        <Form.Item
                            label="姓名"
                            name="stuName"
                            rules={[{
                                require:true,
                                message:'兄弟呀，你还没输入名字呢',
                            },]}
                        >
                            <Input ref="nameRef"/>
                        </Form.Item>
                        <Form.Item
                            label="学号"
                            name="stuId"
                            rules={[{
                                require:true,
                                message:'兄弟，你把学号忘了,真牛马呀'
                            }]}>
                            <Input ref="stuIdRef"/>
                        </Form.Item>

                    </Form>
                </Modal>
            </>

        )
    }
    const columns = [
        {
            title:'姓名',
            dataIndex:'studentName',
            key:'studentName',
            render: text=><a>{text}</a>,
        },
        {
            title:'学号/工号',
            dataIndex:'studentId',
            key:'studentId',
        },
        {
            title:'卡号',
            dataIndex:'studentCardId',
            key:'studentCardId',
        },
        // {
        //     title: '标签',
        //     key: 'tags',
        //     render: tags => (
        //         <>
        //             {tags.map(tag => {
        //                 let color = tag.length > 5 ? 'geekblue' : 'green';
        //                 if (tag === 'student') {
        //                     color = 'green';
        //                 }else if(tag === 'teacher'){
        //                     color = 'orange';
        //                 }else{
        //                     color = 'volcano'
        //                 }
        //                 return (
        //                     <Tag color={color} key={tag}>
        //                         {tag.toLowerCase()}
        //                     </Tag>
        //                 );
        //             })}
        //         </>
        //     ),
        // },
        // {
        //     title:'操作',
        //     key:'action',
        //     dataIndex: 'action',
        //     render:(record)=>(
        //         <Space size="middle">
        //             <a>修改信息</a>
        //             <a>删除</a>
        //         </Space>
        //     )
        // }
        {
            title: 'action',
            key: 'action',
            render: (text, record) => (
                <Space size="middle">
                    <a onClick={showModal}>编辑{record.name}的信息</a>
                    <a>Delete</a>
                </Space>
            ),
        },
    ];
    const data = [
        {
            studentName:'张三',
            studentId:'1907040649',
            studentCardId:'0D19AB82',

        },
        {
            studentName:'张三',
            studentId:'1907040649',
            studentCardId:'0D19AB82',

        },
        {
            studentName:'张三',
            studentId:'1907040649',
            studentCardId:'0D19AB82',

        },
        {
            studentName:'张三',
            studentId:'1907040649',
            studentCardId:'0D19AB82',

        },

    ]
    return(
        <>
            <Table columns={columns} dataSource={result} class="index"/>
            <ModalView/>
        </>
         );
}

export default TableList