import { Cell } from 'fixed-data-table';
import React from 'react';

import styles from '../../styles/CellWithTooltip.scss';

export default function CellWithTooltip({ children, ...props }) {
    return <Cell className={styles.cell} title={children} {...props}>{children}</Cell>;
}

CellWithTooltip.propTypes = {
  children: React.PropTypes.string.isRequired,
};
