import React, { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts'

import { useAuth } from '../../../auth/AuthContext'
import { apiError } from '../../../api/http'
import { reportApi } from '../../../api/reportApi'
import http from '../../../api/http'
import { ErrorBanner, PageHeader } from '../../../components/common/Ui'

const COLORS = ['#0ea5a8', '#0284c7', '#3b82f6', '#7c3aed', '#f59e0b', '#ef4444']

export default function DashboardPage() {
  const auth = useAuth()
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({
    preset: 'MONTH',
    from: '',
    to: '',
    staffId: '',
    roomId: ''
  })

  const loadDashboard = async (nextFilters = filters) => {
    if (!auth.hasRole('OWNER', 'ADMIN')) {
      setLoading(false)
      return
    }

    setLoading(true)
    setError('')
    try {
      const params = {
        preset: nextFilters.preset,
        ...(nextFilters.preset === 'CUSTOM' && nextFilters.from ? { from: nextFilters.from } : {}),
        ...(nextFilters.preset === 'CUSTOM' && nextFilters.to ? { to: nextFilters.to } : {}),
        ...(nextFilters.staffId ? { staffId: nextFilters.staffId } : {}),
        ...(nextFilters.roomId ? { roomId: nextFilters.roomId } : {})
      }
      const response = await reportApi.dashboard(params)
      setData(response.data)
    } catch (e) {
      setError(apiError(e))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadDashboard()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const setPreset = (preset) => {
    const next = { ...filters, preset }
    setFilters(next)
    if (preset !== 'CUSTOM') loadDashboard(next)
  }

  const applyFilters = (e) => {
    e?.preventDefault()
    loadDashboard(filters)
  }

  const exportDashboard = async (format) => {
    setError('')
    try {
      const params = {
        preset: filters.preset,
        ...(filters.preset === 'CUSTOM' && filters.from ? { from: filters.from } : {}),
        ...(filters.preset === 'CUSTOM' && filters.to ? { to: filters.to } : {}),
        ...(filters.staffId ? { staffId: filters.staffId } : {}),
        ...(filters.roomId ? { roomId: filters.roomId } : {})
      }
      const response = await http.get(`/reports/dashboard/export/${format}`, { params, responseType: 'blob' })
      const url = URL.createObjectURL(response.data)
      const link = document.createElement('a')
      link.href = url
      link.download = `ADENT_dashboard.${format}`
      document.body.appendChild(link)
      link.click()
      link.remove()
      setTimeout(() => URL.revokeObjectURL(url), 3000)
    } catch (e) { setError(apiError(e)) }
  }

  const patientMix = useMemo(() => [
    { name: 'Nuevos', value: data?.patientMix?.newPatients ?? 0 },
    { name: 'Recurrentes', value: data?.patientMix?.recurrentPatients ?? 0 }
  ], [data])

  if (auth.isPlatformAdmin) {
    return <div>
      <PageHeader title="Panel de plataforma" subtitle="Administración global de ADENT." />
      <section className="card">
        <h2>Administración de plataforma</h2>
        <p>Este dashboard clínico se muestra por establecimiento. El Super Admin administra organizaciones y tenants.</p>
        <Link className="primary inline" to="/platform">Gestionar organizaciones</Link>
      </section>
    </div>
  }

  return <div className="pro-dashboard">
    <PageHeader
      title="Dashboard de atención y reportes"
      subtitle={`Resumen operativo · ${auth.session?.organizationName || 'ADENT'}`}
    />

    <ErrorBanner message={error} />

    <form className="dashboard-filterbar" onSubmit={applyFilters}>
      <div className="period-tabs" aria-label="Periodo del dashboard">
        <button type="button" className={filters.preset === 'TODAY' ? 'active' : ''} onClick={() => setPreset('TODAY')}>Hoy</button>
        <button type="button" className={filters.preset === 'WEEK' ? 'active' : ''} onClick={() => setPreset('WEEK')}>Semana</button>
        <button type="button" className={filters.preset === 'MONTH' ? 'active' : ''} onClick={() => setPreset('MONTH')}>Mes</button>
        <button type="button" className={filters.preset === 'CUSTOM' ? 'active' : ''} onClick={() => setPreset('CUSTOM')}>Rango personalizado</button>
      </div>

      {filters.preset === 'CUSTOM' && <div className="custom-dates">
        <input type="date" aria-label="Fecha inicial" value={filters.from} onChange={e => setFilters({ ...filters, from: e.target.value })} />
        <span>→</span>
        <input type="date" aria-label="Fecha final" value={filters.to} onChange={e => setFilters({ ...filters, to: e.target.value })} />
      </div>}

      <select aria-label="Filtrar por odontólogo" value={filters.staffId} onChange={e => setFilters({ ...filters, staffId: e.target.value })}>
        <option value="">Todos los odontólogos</option>
        {(data?.dentists || []).map(item => <option key={item.id} value={item.id}>{item.name}</option>)}
      </select>

      <select aria-label="Filtrar por consultorio" value={filters.roomId} onChange={e => setFilters({ ...filters, roomId: e.target.value })}>
        <option value="">Todos los consultorios</option>
        {(data?.rooms || []).map(item => <option key={item.id} value={item.id}>{item.name}</option>)}
      </select>

      <button className="primary dashboard-apply" type="submit">Aplicar filtros</button>
      <button type="button" onClick={() => exportDashboard('pdf')}>Exportar PDF</button>
      <button type="button" onClick={() => exportDashboard('xlsx')}>Exportar Excel</button>
    </form>

    {loading ? <section className="card dashboard-loading">Cargando estadísticas del establecimiento…</section> : <>
      <section className="dashboard-kpis-pro">
        <KpiCard label="Atenciones" value={data?.encounters ?? 0} hint={data?.periodLabel} tone="teal" />
        <KpiCard label="Pacientes nuevos" value={data?.newPatients ?? 0} hint="Registrados en el periodo" tone="blue" />
        <KpiCard label="Citas atendidas" value={data?.completedAppointments ?? 0} hint="Citas completadas" tone="cyan" />
        <KpiCard label="Citas canceladas" value={data?.cancelledAppointments ?? 0} hint="Canceladas en el periodo" tone="red" />
        <KpiCard label="Ingresos" value={data?.incomeAvailable ? formatMoney(data.income) : 'Pendiente'} hint={data?.incomeAvailable ? 'Facturación registrada' : 'Se habilitará con facturación'} tone="green" disabled={!data?.incomeAvailable} />
        <KpiCard label="Personal" value={data?.staff ?? 0} hint="Personal activo" tone="violet" />
      </section>

      <section className="dashboard-analytics-grid">
        <ChartCard title="Atenciones por período" subtitle="Evolución de las atenciones clínicas" className="wide-chart">
          <ResponsiveContainer width="100%" height={260}>
            <AreaChart data={data?.attentionsByPeriod || []} margin={{ top: 10, right: 12, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="label" tick={{ fontSize: 11 }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
              <Tooltip />
              <Area type="monotone" dataKey="value" name="Atenciones" stroke="#0ea5a8" fill="#ccfbf1" strokeWidth={3} />
            </AreaChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Pacientes nuevos por mes" subtitle="Altas de pacientes en los últimos 6 meses">
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={data?.patientsByMonth || []} margin={{ top: 10, right: 8, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="label" tick={{ fontSize: 11 }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
              <Tooltip />
              <Bar dataKey="value" name="Pacientes" fill="#60a5fa" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Atenciones por odontólogo" subtitle="Carga asistencial por profesional">
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={data?.attentionsByDentist || []} layout="vertical" margin={{ top: 0, right: 20, left: 24, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" horizontal={false} />
              <XAxis type="number" allowDecimals={false} />
              <YAxis dataKey="name" type="category" width={115} tick={{ fontSize: 10 }} />
              <Tooltip />
              <Bar dataKey="value" name="Atenciones" fill="#14b8a6" radius={[0, 6, 6, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Atenciones por consultorio" subtitle="Distribución de uso por ambiente">
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={data?.attentionsByRoom || []} dataKey="value" nameKey="name" innerRadius={55} outerRadius={90} paddingAngle={2}>
                {(data?.attentionsByRoom || []).map((_, index) => <Cell key={index} fill={COLORS[index % COLORS.length]} />)}
              </Pie>
              <Tooltip />
              <Legend wrapperStyle={{ fontSize: 11 }} />
            </PieChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Citas atendidas vs. canceladas" subtitle="Comparación por fecha" className="wide-chart">
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={data?.attendedVsCancelled || []} margin={{ top: 10, right: 8, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="label" tick={{ fontSize: 11 }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
              <Tooltip />
              <Legend />
              <Bar dataKey="attended" name="Atendidas" fill="#0ea5a8" radius={[5, 5, 0, 0]} />
              <Bar dataKey="cancelled" name="Canceladas" fill="#ef4444" radius={[5, 5, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Pacientes nuevos vs. recurrentes" subtitle="Pacientes atendidos en el período">
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={patientMix} dataKey="value" nameKey="name" innerRadius={58} outerRadius={90} paddingAngle={3}>
                <Cell fill="#14b8a6" />
                <Cell fill="#93c5fd" />
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Tratamientos realizados" subtitle="Basado en procedimientos registrados en atenciones">
          {(data?.treatments || []).length ? <ResponsiveContainer width="100%" height={260}>
            <BarChart data={data.treatments} layout="vertical" margin={{ top: 0, right: 20, left: 24, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" horizontal={false} />
              <XAxis type="number" allowDecimals={false} />
              <YAxis dataKey="name" type="category" width={115} tick={{ fontSize: 10 }} />
              <Tooltip />
              <Bar dataKey="value" name="Tratamientos" fill="#06b6d4" radius={[0, 6, 6, 0]} />
            </BarChart>
          </ResponsiveContainer> : <EmptyChart text="Aún no existen procedimientos registrados para este período." />}
        </ChartCard>

        <ChartCard title="Ingresos por mes" subtitle="Disponible cuando se integre el módulo de facturación" className="wide-chart finance-chart">
          {data?.incomeAvailable ? <ResponsiveContainer width="100%" height={260}>
            <LineChart data={data?.incomeByMonth || []} margin={{ top: 10, right: 15, left: 5, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="label" />
              <YAxis />
              <Tooltip formatter={(value) => formatMoney(value)} />
              <Line type="monotone" dataKey="value" name="Ingresos" stroke="#10b981" strokeWidth={3} />
            </LineChart>
          </ResponsiveContainer> : <EmptyChart text="No se muestran montos ficticios. Este gráfico se activará cuando ADENT tenga facturación/ingresos reales." />}
        </ChartCard>
      </section>
    </>}
  </div>
}

function KpiCard({ label, value, hint, tone, disabled }) {
  return <article className={`pro-kpi tone-${tone}${disabled ? ' is-disabled' : ''}`}>
    <span>{label}</span>
    <strong>{value}</strong>
    <small>{hint}</small>
  </article>
}

function ChartCard({ title, subtitle, className = '', children }) {
  return <article className={`card pro-chart-card ${className}`}>
    <div className="pro-chart-head">
      <div>
        <h2>{title}</h2>
        <p>{subtitle}</p>
      </div>
    </div>
    <div className="pro-chart-body">{children}</div>
  </article>
}

function EmptyChart({ text }) {
  return <div className="pro-empty-chart">{text}</div>
}

function formatMoney(value) {
  const number = Number(value || 0)
  return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 2 }).format(number)
}
