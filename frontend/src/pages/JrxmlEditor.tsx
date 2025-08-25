
import React from 'react';
const JrxmlEditor: React.FC = () => {
  const [tenant,setTenant] = React.useState('default');
  const [docType,setDocType] = React.useState('factura');
  const [jrxml,setJrxml] = React.useState('');
  const load = async ()=>{
    const r = await fetch(`/templates/${docType}`, { headers: { 'X-Tenant-Id': tenant } });
    setJrxml(r.ok ? await r.text() : '');
  };
  React.useEffect(()=>{ load(); }, [docType, tenant]);
  const save = async ()=>{
    await fetch(`/templates/${docType}`, { method:'PUT', headers: { 'X-Tenant-Id': tenant, 'Content-Type':'text/plain' }, body: jrxml });
    await load();
  };
  const [previewXml, setPreviewXml] = React.useState('');
  const loadSampleJrxml = async ()=>{ const r = await fetch(`/samples/jrxml/${docType}`); setJrxml(await r.text()); };
  const loadSampleXml = async ()=>{ const r = await fetch(`/samples/xml/${docType}`); setPreviewXml(await r.text()); };
  const previewWithXml = async ()=>{
    const res = await fetch(`/ride/render.pdf/${docType}`, { method:'POST', headers: { 'X-Tenant-Id': tenant, 'Content-Type':'application/xml' }, body: previewXml || '<xml/>' });
    const blob = await res.blob(); const url = URL.createObjectURL(blob); const iframe = document.getElementById('pdfframe') as HTMLIFrameElement | null; if(iframe) iframe.src=url;
  };
  return <div style={{padding:16}}>
    <h2>JRXML Editor</h2>
    <div style={{display:'flex', gap:8}}>
      <input value={tenant} onChange={e=>setTenant(e.target.value)} placeholder="tenant" />
      <select value={docType} onChange={e=>setDocType(e.target.value)}>
        <option value="factura">factura</option><option value="notaCredito">notaCredito</option><option value="notaDebito">notaDebito</option>
        <option value="guiaRemision">guiaRemision</option><option value="retencion">retencion</option><option value="liquidacionCompra">liquidacionCompra</option>
      </select>
      <button onClick={save}>Guardar</button>
      <button onClick={loadSampleJrxml}>Cargar muestra JRXML</button>
      <button onClick={loadSampleXml}>Cargar muestra XML</button>
      <button onClick={previewWithXml}>Probar con XML → PDF</button>
    </div>
    <textarea value={jrxml} onChange={e=>setJrxml(e.target.value)} style={{width:'100%', height:240, marginTop:8}} />
    <textarea value={previewXml} onChange={e=>setPreviewXml(e.target.value)} placeholder="XML de ejemplo" style={{width:'100%', height:150, marginTop:8}} />
    <iframe id="pdfframe" title="preview" style={{width:'100%', height:500, border:'1px solid #ccc', marginTop:8}} />
  </div>;
};
export default JrxmlEditor;
