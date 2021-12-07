import {Modal,Form,Input,Button} from 'antd';

import {useState} from "react";

const ModalView = () =>{

    const [visable,setVisable] = useState(false);

    return(
        <Modal
            visible={visable}
            title="编辑信息"
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
                <Form.Item
                    wrapperCol={{
                        offset:8,

                    }}>
                    <Button type="primary" htmlType="submit">
                        提交
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    )
}
export default {ModalView};