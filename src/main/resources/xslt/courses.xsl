<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- Template principal -->
    <xsl:template match="/">
        <html>
            <head>
                <title>Liste des cours</title>
                <link rel="stylesheet" href="/static/style.css"/>
            </head>
            <body>
                <h1>📚 Liste des cours</h1>

                <ul>
                    <!-- Boucle sur chaque course -->
                    <xsl:for-each select="courses/course">
                        <li>
                            <h2><xsl:value-of select="title"/></h2>
                            <p><strong>Auteur :</strong> <xsl:value-of select="author"/></p>
                            <p><strong>Catégorie :</strong> <xsl:value-of select="@category"/></p>
                            <p><strong>Description :</strong> <xsl:value-of select="description"/></p>

                            <!-- Bouton lien vers détail du cours (ex: /courses/1) -->
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('/courses/', @id)"/>
                                </xsl:attribute>
                                Voir le cours
                            </a>
                        </li>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
