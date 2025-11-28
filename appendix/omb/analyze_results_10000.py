import json
import matplotlib.pyplot as plt
import sys
import os

def load_metrics(filename):
    """
    Parses the flat-array JSON format from OpenMessaging Benchmark
    """
    if not os.path.exists(filename):
        print(f"Error: File {filename} not found.")
        return None

    with open(filename, 'r') as f:
        data = json.load(f)

    # We generate a 'time' axis based on the number of data points
    count = len(data.get('publishRate', []))
    time_axis = list(range(1, count + 1))

    return {
        'driver': data.get('driver', 'Unknown'),
        'time': time_axis,
        'throughput': data.get('publishRate', []),
        'p99_pub_latency': data.get('publishLatency99pct', []),
        'p99_e2e_latency': data.get('endToEndLatency99pct', [])
    }

def plot_comparison(rabbit_file, kafka_file):
    rabbit_data = load_metrics(rabbit_file)
    kafka_data = load_metrics(kafka_file)

    if not rabbit_data or not kafka_data:
        return

    # Create a figure with 2 subplots (Latency and Throughput)
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(12, 10))

    # Plot 1: P99 Publish Latency (Responsiveness)
    ax1.plot(rabbit_data['time'], rabbit_data['p99_pub_latency'],
             label='RabbitMQ', marker='o', linestyle='-', color='orange')
    ax1.plot(kafka_data['time'], kafka_data['p99_pub_latency'],
             label='Kafka', marker='x', linestyle='--', color='blue')

    ax1.set_title('Hypothesis 1: P99 Publish Latency (Lower is Better)')
    ax1.set_ylabel('Latency (ms)')
    ax1.set_xlabel('Benchmark Interval')
    ax1.grid(True)
    ax1.legend()

    # Plot 2: Throughput Stability
    ax2.plot(rabbit_data['time'], rabbit_data['throughput'],
             label='RabbitMQ', color='orange')
    ax2.plot(kafka_data['time'], kafka_data['throughput'],
             label='Kafka', color='blue')

    ax2.axhline(y=10000, color='r', linestyle=':', label='Target Rate (10000 msg/s)')

    ax2.set_title('Throughput Stability (Target: 10000 msg/s)')
    ax2.set_ylabel('Messages / Second')
    ax2.set_xlabel('Benchmark Interval')
    ax2.grid(True)
    ax2.legend()

    plt.tight_layout()
    output_file = 'benchmark_comparison_stress.png'
    plt.savefig(output_file)
    print(f"Successfully generated comparison graph: {output_file}")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python analyze_results_10000.py <rabbitmq_result.json> <kafka_result.json>")
        sys.exit(1)

    plot_comparison(sys.argv[1], sys.argv[2])
