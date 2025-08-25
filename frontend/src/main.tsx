import './index.css'

import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App'

createRoot(document.getElementById('root')!).render(
  <BrowserRouter><App/></BrowserRouter>
)


function EDocPage(){
  const create = async () => {
    const tipo = (document.getElementById('tipo') as HTMLSelectElement).value
    const version = (document.getElementById('version') as HTMLInputElement).value
    const clave = (document.getElementById('clave') as HTMLInputElement).value
    const ruc = (document.getElementById('ruc') as HTMLInputElement).value
    const res = await fetch('/edoc/create',{method:'POST', headers:{'Content-Type':'application/json','Authorization':'Bearer '+localStorage.getItem('jwt')}, body: JSON.stringify({tipo, version, claveAcceso: clave, ruc})})
    alert(await res.text())
  }
  return (<div className='p-4'>
    <h3 className='text-lg font-bold mb-2'>Emitir eDoc</h3>
    <div className='flex gap-2 items-center'>
      <select id="tipo">
        <option>FACTURA</option><option>NOTA_CREDITO</option><option>NOTA_DEBITO</option>
        <option>GUIA_REMISION</option><option>RETENCION</option><option>LIQUIDACION</option>
      </select>
      <input id="version" placeholder="1.1.0" defaultValue="1.1.0"/>
      <input id="clave" placeholder="Clave de Acceso"/>
      <input id="ruc" placeholder="RUC"/>
      <button onClick={create}>Crear</button>
    </div>
  </div>)
}

function useApi(){ return { base: '', auth: 'Bearer '+localStorage.getItem('jwt') }; }

function Companies(){
  const [list,setList] = React.useState<any[]>([]);
  const [ruc,setRuc] = React.useState('1790012345001');
  const [razon,setRazon] = React.useState('ACME SA');
  const load = async ()=>{ const r = await fetch('/companies'); setList(await r.json()); }
  React.useEffect(()=>{ load(); },[]);
  const create = async ()=>{ await fetch('/companies',{method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ruc, razonSocial: razon})}); load(); }
  const upload = async (id:string, f:File)=>{ const fd = new FormData(); fd.append('file', f); await fetch('/companies/'+id+'/cert',{method:'POST', body: fd}); alert('Cert subido'); }
  return <div className='p-4'><h3 className='font-bold'>Companies</h3>
    <div className='flex gap-2'><input value={ruc} onChange={e=>setRuc(e.target.value)} placeholder='RUC'/><input value={razon} onChange={e=>setRazon(e.target.value)} placeholder='Razón social'/><button onClick={create}>Crear</button></div>
    <table><thead><tr><th>RUC</th><th>Razón Social</th><th>Cert</th></tr></thead>
      <tbody>{list.map(c => <tr key={c.id}><td>{c.ruc}</td><td>{c.razonSocial}</td><td><input type='file' onChange={e=> e.target.files && upload(c.id, e.target.files[0])}/></td></tr>)}</tbody>
    </table>
  </div>
}

function Webhooks(){
  const [list,setList] = React.useState<any[]>([]);
  const [url,setUrl] = React.useState('http://localhost:9999/webhook');
  const [secret,setSecret] = React.useState('secret');
  const load = async ()=>{ const r = await fetch('/webhooks/subscriptions'); setList(await r.json()); }
  React.useEffect(()=>{ load(); },[]);
  const add = async ()=>{ await fetch('/webhooks/subscriptions',{method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({url, secret})}); load(); }
  return <div className='p-4'><h3 className='font-bold'>Webhooks</h3>
    <div className='flex gap-2'><input value={url} onChange={e=>setUrl(e.target.value)} className='w-[400px]'/><input value={secret} onChange={e=>setSecret(e.target.value)}/><button onClick={add}>Agregar</button></div>
    <ul>{list.map((x:any)=><li key={x.id}>{x.url}</li>)}</ul>
  </div>
}

function Documents(){
  const [items,setItems] = React.useState<any[]>([]);
  React.useEffect(()=>{ fetch('/einvoice').then(r=>r.json()).then(setItems); },[]);
  return <div className='p-4'><h3 className='font-bold'>Documentos</h3>
    <table><thead><tr><th>ID</th><th>Estado</th><th>Acciones</th></tr></thead>
    <tbody>{items.map((d:any)=><tr key={d.id}><td>{d.id}</td><td>{d.status}</td><td><a href={'/einvoice/'+d.id+'/download?type=xml'} target='_blank'>XML</a> | <a href={'/einvoice/'+d.id+'/download?type=ride'} target='_blank'>RIDE</a></td></tr>)}</tbody>
    </table></div>
}

function Audit(){
  const [items,setItems] = React.useState<any[]>([]);
  React.useEffect(()=>{ fetch('/audit').then(r=>r.json()).then(setItems); },[]);
  return <div className='p-4'><h3 className='font-bold'>Audit</h3>
    <ul>{items.map((a:any)=><li key={a.id}>{a.at} — {a.action} {a.entity}</li>)}</ul>
  </div>
}

function ExportDocs(){
  const [docs,setDocs] = React.useState<any[]>([]);
  const [sel,setSel] = React.useState<Record<number,boolean>>({});
  React.useEffect(()=>{ fetch('/edoc').then(r=>r.json()).then(setDocs); },[]);
  const toggle=(id:number)=> setSel({...sel, [id]: !sel[id]});
  const exportZip = async (type:'xml'|'pdf') => {
    const ids = Object.entries(sel).filter(([,v])=>v).map(([k])=>Number(k));
    const r = await fetch('/edoc/export?type='+type,{method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(ids)});
    const blob = await r.blob(); const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href=url; a.download='edocs-'+type+'.zip'; a.click();
  }
  return <div className='p-4'><h3 className='font-bold'>Exportar Documentos</h3>
    <table><thead><tr><th></th><th>ID</th><th>Tipo</th><th>Estado</th></tr></thead>
      <tbody>{docs.map((d:any)=><tr key={d.id}><td><input type='checkbox' checked={!!sel[d.id]} onChange={()=>toggle(d.id)}/></td><td>{d.id}</td><td>{d.tipo}</td><td>{d.estado}</td></tr>)}</tbody>
    </table>
    <div className='mt-2 flex gap-2'><button onClick={()=>exportZip('xml')}>Exportar XML</button><button onClick={()=>exportZip('pdf')}>Exportar PDF</button></div>
  </div>
}
