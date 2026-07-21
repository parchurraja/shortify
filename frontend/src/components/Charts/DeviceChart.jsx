import React from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

export const DeviceChart = ({ deviceData = {} }) => {
  const labels = Object.keys(deviceData).length > 0 ? Object.keys(deviceData) : ['Desktop', 'Mobile', 'Tablet'];
  const values = Object.keys(deviceData).length > 0 ? Object.values(deviceData) : [0, 0, 0];

  const data = {
    labels,
    datasets: [
      {
        data: values,
        backgroundColor: ['#6366f1', '#a855f7', '#38bdf8', '#f43f5e'],
        borderWidth: 0,
        hoverOffset: 6,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          color: '#94a3b8',
          usePointStyle: true,
          padding: 16,
          font: {
            size: 12,
          },
        },
      },
      tooltip: {
        backgroundColor: '#0f172a',
        padding: 12,
        borderRadius: 12,
      },
    },
    cutout: '70%',
  };

  return (
    <div className="h-64 w-full flex items-center justify-center">
      <Doughnut data={data} options={options} />
    </div>
  );
};

export default DeviceChart;
