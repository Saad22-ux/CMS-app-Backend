<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <head>
                <title>Liste des utilisateurs</title>
                <link rel="stylesheet" href="/static/style.css"/>
            </head>
            <body>
                <h1>👥 Liste des utilisateurs</h1>
                <ul>
                    <xsl:for-each select="users/user">
                        <li>
                            <h2><xsl:value-of select="name"/></h2>
                            <p><strong>Email :</strong> <xsl:value-of select="email"/></p>
                            <p><strong>Rôle :</strong> <xsl:value-of select="@role"/></p>
                            <p><strong>Cours inscrits :</strong>
                                <xsl:for-each select="courses/courseRef">
                                    <xsl:value-of select="@id"/>
                                    <xsl:if test="position() != last()">, </xsl:if>
                                </xsl:for-each>
                            </p>
                        </li>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
