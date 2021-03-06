import axios from 'axios';
import { async } from 'q';
import { useCallback, useEffect, useState } from 'react';
import {Button, Card} from 'react-bootstrap';
import "./ApplicationResult.css";

function ApplicationResult(){

    const memberName = useState(localStorage.getItem("memberName"))
    const [checkLogin, setCheckLogin] = useState(false)
    const [checkApply, setCheckApply] = useState(false)
    const [checkServcie, setCheckService] = useState(false)
    const [collectDtoList, setCollectDtoList] = useState(
        [
            {   
                collectId : 0,
                collecttype : "wash",
                collectRequestDate : "2022-05-03 09:26:30.000000",
                collectWithdrawDate : null
            }
        ]
    )
    useEffect(()=>{
        let Authorization = localStorage.getItem("authorization")
        let RefreshToekn = localStorage.getItem("refreshtoken")
        async function getResult(){
            await axios({
                method: "get",
                url : "/v1/api/member",
                headers : {
                    "Authorization" : Authorization,
                    "RefreshToken" : RefreshToekn 
                }
            }).then((res) =>{
                console.log("loginCheck")
                console.log(res)
                async function getPlan(){
                    await axios({
                        method: "get",
                        url : "/v1/api/plan",
                        headers : {
                            "Authorization" : Authorization,
                            "RefreshToken" : RefreshToekn 
                        }
                    }).then((res)=>{
                        axios({
                            method : "get",
                            url : "/v1/api/order/collect/check",
                            headers : {
                                "Authorization" : Authorization,
                                "RefreshToken" : RefreshToekn 
                            }
                        }).then((res)=>{
                            if(res.status == 204){
                                window.location.href = "/applicationInfo"
                            }
                                else
                                    setCollectDtoList(res.data)
                            })
                        }
                    ).catch((err)=>{
                        window.location.href="/pleaseService"
                    })
                }
                getPlan();
            }).catch((err)=>{
                    localStorage.removeItem("memberName")
                    localStorage.removeItem("authorization");
                    localStorage.removeItem("refreshtoken");
                    window.location.replace("/pleaseLogin")
            })
        }
        getResult();
    },[])
        
   
    return(
        <div className='application_container'>
            <Card className='result test-center'>
                <h2>?????? ?????? ??????</h2>
                <Button href='/applicationDetail' variant='success' className='button detail_btn'>????????? ??????</Button>
                <Button href='/' variant='success' className='button cancel_btn'>??????</Button>
            </Card>  
        </div>
    )
}

export default ApplicationResult;