import { Cell } from 'fixed-data-table';
import React from 'react';

import styles from '../styles/ProgressCell.scss';

export default function ProgressCell({ progress, ...props }) {
    return <Cell className={styles.cell} {...props}>
      <div className={styles.progressBar} style={{ width: `${progress}%` }} />
      <div className={styles.text}>{progress}%</div>
    </Cell>;
}

ProgressCell.propTypes = {
  progress: React.PropTypes.number.isRequired,
};
