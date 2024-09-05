function generate(id, name, imageUrl, line1, line2, line3) {
  const col = document.createElement('div');
  col.className = 'col';

  const card = document.createElement('div');
  card.className = 'card';

  const link = document.createElement('a');
  link.href = `/products/${id}`;
  link.style.textDecoration = 'none';
  link.style.color = 'inherit';

  const img = document.createElement('img');
  img.className = 'card-img-top';
  img.alt = name;
  img.src = imageUrl;
  img.style.height = '200px';
  img.style.objectFit = 'cover';
  img.loading = 'lazy';
  link.appendChild(img);

  const cardBody = document.createElement('div');
  cardBody.className = 'card-body';

  const cardTitle = document.createElement('h5');
  cardTitle.className = 'card-title';
  cardTitle.textContent = name;
  cardTitle.style.display = '-webkit-box';
  cardTitle.style.webkitLineClamp = '2';
  cardTitle.style.webkitBoxOrient = 'vertical';
  cardTitle.style.overflow = 'hidden';
  cardTitle.style.textOverflow = 'ellipsis';
  cardTitle.style.height = '48px';
  link.appendChild(cardTitle);

  cardBody.appendChild(link);

  const cardText = document.createElement('p');
  cardText.className = 'card-text';
  cardText.innerHTML = `
        ${line1}<br>
        ${line2}<br>
        ${line3}
      `;
  cardBody.appendChild(cardText);

  card.appendChild(cardBody);
  col.appendChild(card);
  return col;
}
