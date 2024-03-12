#!/bin/bash

SPOTIFY_PARTNER_URL='https://api-partner.spotify.com'

BEARER_TOKEN='BQAGINeIk3IkoJf0phjxjU2iIidTV4vdjA58xzqWF3ieNdYvjs5akLnhy7jdYLAFrUPwAC_5cgLpk6iQ0xsfUW6mG_xvuqQj5XPYB8CG57IueQIyl_38N0-iwsV5277n1yLwkXatifQ2rqqdIycCGDSPuPAUSr-cELr0fZeNTeHbrAD4ZV_p_2wAzyTNGI4w1e7RjehlXBnaQahA2Uz9PyoBweuSO1jSN6-9za-QAa9lq4DgKY86dJ-XSPYtZqg1GQAXy8LDXIBhu0YsnMWzizNXhaXxjm0QQgwLk7q1RbtLm3bCFMxWXrn0p3GnBcH38BjyoDJfqbgCXU41rbFDPmbHYTsrIPNX'


OPERATION_NAME='libraryV3'


SPOTIFY_QUERY="operationName=${OPERATION_NAME}&variables=%7B%22filters%22%3A%5B%5D%2C%22order%22%3A%22Recently+Added%22%2C%22textFilter%22%3A%22%22%2C%22features%22%3A%5B%22LIKED_SONGS%22%2C%22YOUR_EPISODES%22%5D%2C%22limit%22%3A50%2C%22offset%22%3A0%2C%22flatten%22%3Afalse%2C%22expandedFolders%22%3A%5B%5D%2C%22folderUri%22%3A%22spotify%3Auser%3A31xyt7khqn7ujom2jvmttstq2r44%3Afolder%3A41642576effc8bfe%22%2C%22includeFoldersWhenFlattening%22%3Atrue%2C%22withCuration%22%3Afalse%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22808f0e4288442f2cc56e25e2b80029d873316a40c2d63952d20ac40c9f049d9e%22%7D%7D"

curl -L \
-H "Authorization: Bearer $BEARER_TOKEN" \
"${SPOTIFY_PARTNER_URL}/pathfinder/v1/query?${SPOTIFY_QUERY}"
