import collections

def analyze_animation(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"Ошибка: Файл {file_path} не найден.")
        return

    # Разделяем на кадры по маркеру
    raw_frames = content.split('===FRAME===')
    
    # Очищаем от пустых элементов (которые могут возникнуть в начале или конце файла)
    frames = []
    for i, f in enumerate(raw_frames):
        lines = f.strip('\n').split('\n')
        # Игнорируем совсем пустые блоки
        if len(lines) == 1 and lines[0].strip() == '':
            continue
        
        # Вычисляем размеры
        height = len(lines)
        width = max(len(line) for line in lines) if lines else 0
        
        frames.append({
            'index': i, 
            'content': lines,
            'width': width,
            'height': height
        })

    if not frames:
        print("Кадры не найдены.")
        return

    # Определяем "правильный" размер (тот, который встречается чаще всего)
    sizes = [(f['width'], f['height']) for f in frames]
    size_counts = collections.Counter(sizes)
    most_common_size = size_counts.most_common(1)[0][0]
    
    expected_w, expected_h = most_common_size

    print(f"Стандартный размер кадра (чаще всего): {expected_w}x{expected_h}")
    print("-" * 30)

    found_errors = False
    for f in frames:
        if f['width'] != expected_w or f['height'] != expected_h:
            found_errors = True
            print(f"ВНИМАНИЕ: Кадр №{f['index']} имеет неверный размер!")
            print(f"Размер: {f['width']}x{f['height']} (Ожидалось: {expected_w}x{expected_h})")
            print("Содержимое кадра:")
            print("---START---")
            for line in f['content']:
                # Печатаем строку, заменяя пробелы точками в конце для видимости ширины
                print(line)
            print("---END---")
            print("\n" + "="*50 + "\n")

    if not found_errors:
        print("Все кадры имеют одинаковый размер.")

if __name__ == "__main__":
    analyze_animation('duch-animation.txt')