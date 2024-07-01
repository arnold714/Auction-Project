import './App.css'
import axios from "axios"
import {useState} from "react"

function App() {
  const [inputs, setInputs] = useState({id:'', pwd:''})
  const {id,pwd} = inputs //inputs에 멤버변수랑 자동으로 매칭시스템
  const onChange = (e) => {// e:방금 발생한 이벤트 객체
    const {name, value} = e.target; //event가 발생한 곳
    setInputs({
      ...inputs,
      [name]: value
    })

  }
  const login = () =>{
    axios.post('http://localhost:8080/login',{},{params:{id:id, pwd:pwd}})
    .then(function(res){
      if(res.status===200){
        if(res.data){
          if(res.data.flag){
            alert("로그인 성공")
            alert('id:'+res.data.id+'/ type:'+res.data.type)
          }else{
            alert("로그인 실패")
          }
        } //응답 값
      }else{
        alert("error")
      }
    })
  }
  return (
    <div className="App">
      <h3>카페 로그인</h3>
       {/* inputs에 있는 id, pwd 사용 */}
      id:<input type="text" name="id" onChange={onChange} value={id}></input><br/> 
      pwd:<input type="password" name="pwd" onChange={onChange} value={pwd}></input><br/>      
      <button onClick={login}>로그인</button>
    </div>
  );
}

export default App;
