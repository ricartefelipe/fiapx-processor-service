#!/bin/bash
set -euo pipefail

output_pattern=""
for arg in "$@"; do
  if [[ "$arg" == *.jpg ]] || [[ "$arg" == *%04d* ]]; then
    output_pattern="$arg"
  fi
done

if [[ -z "$output_pattern" ]]; then
  echo "fake ffmpeg: padrão de saída não encontrado" >&2
  exit 1
fi

dir="$(dirname "$output_pattern")"
file="${dir}/frame_0001.jpg"
mkdir -p "$dir"
printf 'frame' > "$file"
exit 0
