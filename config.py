def load_config(config_file='IP_PORT_ADDRESS.txt'):
    config = {}
    try:
        with open(config_file, 'r') as f:
            for line in f:
                if '=' in line:
                    key, value = line.split('=')
                    # 양쪽 공백을 제거하고, 따옴표가 있다면 제거
                    config[key.strip()] = value.strip().strip('"').strip("'")
    except FileNotFoundError:
        raise Exception(f"Configuration file {config_file} not found")
    
    if 'IP' not in config or 'PORT' not in config:
        raise Exception("IP or PORT not found in config file")
    
    return config