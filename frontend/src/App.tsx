
import React, { useEffect, useState } from 'react'
import { Routes, Route, Link, useNavigate } from 'react-router-dom'
import JrxmlEditor from './pages/JrxmlEditor'

const API = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

function useToken(){ 
  const [t,setT] = useState(localStorage.getItem('token')||'')
  return { token:t, set:(v:string)=>{ localStorage.setItem('token',v); setT(v)} }
}

function Login(){
  const { set } = useToken(); const nav = useNavigate();
  const [email,setEmail] = useState('admin@local'); const [password,setPassword] = useState('admin');
  const onSubmit = async (e:React.FormEvent)=>{
    e.preventDefault();
    const r = await fetch(API+'/auth/login',{method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({email,password})});
    const j = await r.json(); set(j.token); nav('/');
  }
  return <form onSubmit={onSubmit} style={{display:'grid',gap:8,maxWidth:320,margin:'40px auto'}}>
    <h2>Login</h2>
    <input value={email} onChange={e=>setEmail(e.target.value)} placeholder="email"/>
    <input value={password} onChange={e=>setPassword(e.target.value)} placeholder="password" type="password"/>
    <button>Sign in</button>
  </form>
}

function Home(){
  const { token } = useToken();
  const [branding,setBranding] = useState<any>({name:'...',primaryColor:'#0ea5e9'})
  useEffect(()=>{ fetch(API+'/branding',{headers:{'Authorization':'Bearer '+token,'X-Tenant-ID':'demo'}}).then(r=>r.json()).then(setBranding)},[token])
  return <div>
    <header style={{padding:16, background:branding.primaryColor, color:'#fff'}}>
      <b>{branding.name}</b> — Facturación
      <nav style={{float:'right'}}>
        <Link to="/">Docs</Link> | <Link to="/admin/companies">Companies</Link> | <Link to="/docs">Docs</Link> | <Link to="/webhooks">Webhooks</Link> | <Link to="/audit">Audit</Link>
      </nav>
    </header>
    <div style={{padding:16}}>
      <h3>Documentos</h3>
      <CreateDemo/>
    </div>
  </div>
}

function CreateDemo(){
  const [id,setId] = useState<string>(''); const { token } = useToken();
  const headers = {'Authorization':'Bearer '+token,'X-Tenant-ID':'demo','Content-Type':'application/json'}
  const create = async ()=>{
    const r = await fetch(API+'/einvoice',{method:'POST',headers,body: JSON.stringify({ruc:'1790012345001',secuencial:'000000123',claveAcceso:'01234'})})
    const j = await r.json(); setId(j.id);
  }
  const send = async ()=>{ await fetch(`${API}/einvoice/${id}/send`,{method:'POST',headers}) }
  const auth = async ()=>{ await fetch(`${API}/einvoice/${id}/authorize`,{method:'POST',headers}) }
  return <div>
    <button onClick={create}>Crear</button>{" "}
    <button onClick={send} disabled={!id}>Enviar</button>{" "}
    <button onClick={auth} disabled={!id}>Autorizar</button>
    <div>ID: {id}</div>
  </div>
}

export default function App(){
  return <Routes>
  <Route path="/jrxml" element={<JrxmlEditor/>} />
    <Route path="/login" element={<Login/>}/>
    <Route path="/" element={<Home/>}/>
    <Route path="/docs" element={<Documents/>}/>
  
    <Route path="/admin/companies" element={<Companies/>}/>
    <Route path="/webhooks" element={<Webhooks/>}/>
    <Route path="/audit" element={<Audit/>}/>
  </Routes>

}


function useApi(){ 
  const API = (import.meta.env.VITE_API_BASE || 'http://localhost:8080'); 
  const token = localStorage.getItem('token')||'';
  const headers:any = {'Authorization':'Bearer '+token,'X-Tenant-ID':'demo'};
  return { API, headers };
}

function Companies(){
  const { API, headers } = useApi();
  const [list,setList] = React.useState<any[]>([]);
  const [ruc,setRuc] = React.useState('1790012345001');
  const [razon,setRazon] = React.useState('ACME SA');
  const load = async ()=>{ const r = await fetch(API+'/companies',{headers}); setList(await r.json()); }
  React.useEffect(()=>{ load(); },[]);
  const create = async ()=>{
    const r = await fetch(API+'/companies',{method:'POST', headers:{...headers,'Content-Type':'application/json'}, body: JSON.stringify({ruc, razonSocial: razon})});
    await r.json(); load();
  }
  const upload = async (id:string, file:File)=>{
    const fd = new FormData(); fd.append('file', file);
    await fetch(API+`/companies/${id}/cert`, {method:'POST', headers, body: fd});
    alert('Cert uploaded');
  }
  return <div style={{padding:16}}>
    <h3>Companies</h3>
    <div style={{display:'flex', gap:8}}>
      <input value={ruc} onChange={e=>setRuc(e.target.value)} placeholder="RUC"/>
      <input value={razon} onChange={e=>setRazon(e.target.value)} placeholder="Razón social"/>
      <button onClick={create}>Create</button>
    </div>
    <table><thead><tr><th>RUC</th><th>Razón Social</th><th>Certificado</th></tr></thead>
      <tbody>
        {list.map(c=> <tr key={c.id}>
          <td>{c.ruc}</td><td>{c.razon_social}</td>
          <td><input type="file" onChange={e=> e.target.files && upload(c.id, e.target.files[0]) }/></td>
        </tr>)}
      </tbody>
    </table>
  </div>
}

function Webhooks(){
  const { API, headers } = useApi();
  const [list,setList] = React.useState<any[]>([]);
  const [url,setUrl] = React.useState('http://localhost:9999/endpoint');
  const [secret,setSecret] = React.useState('secret');
  const load = async ()=>{ const r = await fetch(API+'/webhooks/subscriptions',{headers}); setList(await r.json()); }
  React.useEffect(()=>{ load(); },[]);
  const create = async ()=>{
    await fetch(API+'/webhooks/subscriptions',{method:'POST', headers:{...headers,'Content-Type':'application/json'}, body: JSON.stringify({url,secret})});
    load();
  }
  return <div style={{padding:16}}>
    <h3>Webhooks</h3>
    <div style={{display:'flex', gap:8}}>
      <input value={url} onChange={e=>setUrl(e.target.value)} placeholder="https://..." style={{width:400}}/>
      <input value={secret} onChange={e=>setSecret(e.target.value)} placeholder="secret"/>
      <button onClick={create}>Add</button>
    </div>
    <ul>{list.map((s:any)=><li key={s.id}>{s.url}</li>)}</ul>
  </div>
}

function Audit(){
  const { API, headers } = useApi();
  const [items,setItems] = React.useState<any[]>([]);
  React.useEffect(()=>{ fetch(API+'/audit',{headers}).then(r=>r.json()).then(setItems); },[]);
  return <div style={{padding:16}}>
    <h3>Audit</h3>
    <ul>{items.map((a:any)=><li key={a.id}>{a.at} — {a.action} {a.entity} {a.entity_id}</li>)}</ul>
  </div>
}


function Documents(){
  const { API, headers } = useApi();
  const [docs,setDocs] = React.useState<any[]>([]);
  React.useEffect(()=>{ fetch(API+'/einvoice',{headers}).then(r=>r.json()).then(setDocs); },[]);
  return <div style={{padding:16}}>
    <h3>Documents</h3>
    <table><thead><tr><th>ID</th><th>Estado</th><th>Acciones</th></tr></thead>
      <tbody>{docs.map(d=><tr key={d.id}>
        <td>{d.id}</td><td>{d.estado}</td>
        <td>
          <a href={`${API}/einvoice/${d.id}/download?type=xml`} target="_blank">XML</a>{" | "}
          <a href={`${API}/einvoice/${d.id}/download?type=ride`} target="_blank">RIDE</a>
        </td>
      </tr>)}</tbody>
    </table>
  </div>
}
